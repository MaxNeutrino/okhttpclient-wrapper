package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.frame.RequestMethod
import neutrino.project.clientwrapper.frame.ResponseConsumer
import neutrino.project.clientwrapper.frame.content.Content
import neutrino.project.clientwrapper.frame.content.ContentConverter
import neutrino.project.clientwrapper.frame.content.JsonContent
import neutrino.project.clientwrapper.frame.content.JsonContentConverter
import neutrino.project.clientwrapper.frame.processor.params.CountableParamsProcessor
import neutrino.project.clientwrapper.frame.processor.params.ParamsProcessor
import neutrino.project.clientwrapper.frame.processor.params.SingleParamsProcessor
import neutrino.project.clientwrapper.params
import java.util.concurrent.CompletableFuture

class SingleMethodProcessor<T : Any>(
		private val method: RequestMethod<*>,
		private val client: Client,
		private val contents: List<Content>,
		private val jsonContent: JsonContent?
) : AbstractRequestMethodProcessor<T>(method.isCountableEnabled) {

	private val contentConverter = ContentConverter()

	private val jsonConverter by lazy { JsonContentConverter() }

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
				.let {
					if(jsonContent != null) {
						injectJsonParam(it.toMutableMap())
					} else it
				}
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

	private fun injectJsonParam(namedParams: MutableMap<String, Params>): Map<String, Params> {
		val json = jsonConverter.convert(jsonContent!!) ?: ""
		namedParams["json"] = params("" to json)
		return namedParams
	}
}