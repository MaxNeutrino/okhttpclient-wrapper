package neutrino.project.clientwrapper.frame.processor.params

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.frame.RequestMethod
import neutrino.project.clientwrapper.frame.RequestMethodModel
import neutrino.project.clientwrapper.frame.ResponseConsumer
import neutrino.project.clientwrapper.util.ext.executeAsync
import okhttp3.Request
import java.util.concurrent.CompletableFuture


class SingleParamsProcessor<T>(
		private val method: RequestMethod<*>,
		private val client: Client,
		private val modifications: Map<String, (Params, RequestMethodModel) -> RequestMethodModel>
) : ParamsProcessor<T>() {

	private var isInterrupt = false

	@Suppress("UNCHECKED_CAST")
	override fun process(namedParams: Map<String, Params>): ResponseConsumer<T> {
		val methodModel = createMethodModel(namedParams)
		val call = client.processAndSend(methodModel.build())!!

		if (isInterrupt) {
			call.cancel()
			return ResponseConsumer()
		}

		return applyMapper(method.responseMapper, call)
	}

	override fun processAsync(namedParams: Map<String, Params>): CompletableFuture<ResponseConsumer<T>> {
		val methodModel = createMethodModel(namedParams)
		val call = client.processAndSend(methodModel.build())!!

		if (isInterrupt) {
			call.cancel()
			return CompletableFuture.supplyAsync({ ResponseConsumer<T>() })
		}

		return call.executeAsync()
				.thenApply {
					applyMapper(method.responseMapper, it)
				}
	}

	private fun createMethodModel(namedParams: Map<String, Params>): RequestMethodModel {
		var methodModel = RequestMethodModel(requestBuilder = Request.Builder(), apiUrl = method.url,
				customUrl = method.customUrl, baseUrl = client.getBaseUrl())

		methodModel.setMethod(method)

		modifications.forEach { name, func ->
			if (namedParams.containsKey(name))
				methodModel = func(namedParams[name]!!, methodModel)
		}
		return methodModel
	}

	override fun interrupt() {
		isInterrupt = true
	}
}