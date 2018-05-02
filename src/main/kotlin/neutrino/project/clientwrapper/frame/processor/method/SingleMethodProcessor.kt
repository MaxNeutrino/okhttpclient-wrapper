package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.frame.RequestMethod
import neutrino.project.clientwrapper.frame.ResponseConsumer
import neutrino.project.clientwrapper.frame.content.Content
import neutrino.project.clientwrapper.frame.content.ContentConverter
import neutrino.project.clientwrapper.frame.processor.params.CountableParamsProcessor
import neutrino.project.clientwrapper.frame.processor.params.ParamsProcessor
import neutrino.project.clientwrapper.frame.processor.params.SingleParamsProcessor
import java.util.concurrent.CompletableFuture


class SingleMethodProcessor<T : Any>(
		private val method: RequestMethod<*>,
		private val client: Client,
		private val contents: List<Content>
) : AbstractRequestMethodProcessor<T>(method.isCountableEnabled) {

	private val contentConverter = ContentConverter()

	private lateinit var paramsProcessor: ParamsProcessor<T>

	override fun process(): ResponseConsumer<T> {
		val params = createParams()
		setUpParamsProcessor()
		return paramsProcessor.process(params)
	}

	override fun processAsync(): CompletableFuture<ResponseConsumer<T>> {
		val params = createParams()
		setUpParamsProcessor()
		return paramsProcessor.processAsync(params)
	}

	override fun interrupt() {
		paramsProcessor.interrupt()
	}

	private fun createParams(): Map<String, Params> {
		return contents.map { it.name to it }
				.map { it.first to contentConverter.convert(it.second) }
				.toMap()
	}

	private fun setUpParamsProcessor() {
		val countable = findCountable(contents)

		paramsProcessor = if (countable != null) {
			CountableParamsProcessor(method, client, Pair(countable.name, countable.countable!!),
					contentModifications)
		} else {
			SingleParamsProcessor(method, client, contentModifications)
		}
	}
}