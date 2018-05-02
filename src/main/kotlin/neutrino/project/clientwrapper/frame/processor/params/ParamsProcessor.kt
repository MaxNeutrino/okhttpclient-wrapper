package neutrino.project.clientwrapper.frame.processor.params

import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.frame.Interruptable
import neutrino.project.clientwrapper.frame.ResponseConsumer
import neutrino.project.clientwrapper.frame.ResponseMapper
import neutrino.project.clientwrapper.util.exception.ResponseMapperNotFoundException
import okhttp3.Call
import okhttp3.Response
import okhttp3.ResponseBody
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


interface ParamsProcessor<T> : Interruptable {

	fun process(namedParams: Map<String, Params>): ResponseConsumer<T>

	fun processAsync(namedParams: Map<String, Params>): CompletableFuture<ResponseConsumer<T>>

	fun cloneResponse(response: Response, body: ResponseBody?): Response? {
		body ?: return null
		return response.newBuilder()
				.body(
						ResponseBody.create(body.contentType(),
								body.contentLength(),
								body.source())
				).build()
	}

	fun applyMapper(mapperClass: KClass<out ResponseMapper<*>>?, call: Call): ResponseConsumer<T> {
		return if (mapperClass != null) {
			val mapper = mapperClass.primaryConstructor?.call() ?: throw ResponseMapperNotFoundException()
			val responseModel = mapper.map(call.execute())
			ResponseConsumer(responseModel = responseModel as T)
		} else {
			ResponseConsumer(call = call)
		}
	}

	@Suppress("UNCHECKED_CAST")
	@Synchronized
	fun applyMapper(mapperClass: KClass<out ResponseMapper<*>>?, response: Response): ResponseConsumer<T> {
		return if (mapperClass != null) {
			val mapper = mapperClass.primaryConstructor?.call() ?: throw ResponseMapperNotFoundException()
			val responseModel = mapper.map(response)
			ResponseConsumer(responseModel = responseModel as T)
		} else {
			ResponseConsumer(response = response)
		}
	}
}