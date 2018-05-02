package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.frame.Interruptable
import neutrino.project.clientwrapper.frame.RequestMethod
import neutrino.project.clientwrapper.frame.ResponseConsumer
import neutrino.project.clientwrapper.frame.content.Content
import neutrino.project.clientwrapper.util.exception.IterableModelNotFoundException
import neutrino.project.clientwrapper.util.ext.collect
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors
import java.util.stream.Stream


class IterableMethodProcessor<T : Any>(
		private val method: RequestMethod<*>,
		private val client: Client,
		private val iterableContent: Content,
		private val contents: List<Content>
) : AbstractRequestMethodProcessor<T>(method.isCountableEnabled) {

	@Volatile
	private var isInterrupt = false

	private val interruptableList: MutableList<Interruptable> = mutableListOf()

	@Throws(IterableModelNotFoundException::class, ClassCastException::class)
	@Suppress("UNCHECKED_CAST")
	override fun process(): ResponseConsumer<T> {
		return toResponses(SingleMethodProcessor<T>::process).collect()
	}

	override fun processAsync(): CompletableFuture<ResponseConsumer<T>> {
		return toResponses(SingleMethodProcessor<T>::processAsync).collect()
				.thenApply { it.collect() }
	}

	private fun createStream(iterableModel: Iterable<*>): Stream<*> {
		return if (method.parallel) {
			iterableModel.toList().parallelStream()
		} else {
			iterableModel.toList().stream()
		}
	}

	private fun <R: Any> toResponses(processor: (SingleMethodProcessor<T>) -> R): List<R> {
		val model = iterableContent.model ?: throw IterableModelNotFoundException()

		model as Iterable<*>

		val contentMap = contents.map { it.name to it }.toMap().toMutableMap()

		val modelStream = createStream(model)

		return modelStream.map { m ->
			if (isInterrupt)
				return@map null

			val modelContent = Content(
					name = iterableContent.name,
					params = iterableContent.params,
					map = iterableContent.map,
					model = m,
					countable = iterableContent.countable,
					modelConverter = iterableContent.modelConverter
			)

			contentMap[modelContent.name] = modelContent

			val singleMethodProcessor = SingleMethodProcessor<T>(method, client, contentMap.values.toList())
			interruptableList.add(singleMethodProcessor)

			processor(singleMethodProcessor)
		}.collect(Collectors.toList())
				.filterNotNull()
	}

	override fun interrupt() {
		isInterrupt = true
		interruptableList.forEach(Interruptable::interrupt)
	}


}