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


abstract class ParamsProcessor<T> : Interruptable {

	abstract fun process(namedParams: Map<String, Params>): ResponseConsumer<T>

	abstract fun processAsync(namedParams: Map<String, Params>): CompletableFuture<ResponseConsumer<T>>

	fun cloneResponse(response: Response, body: ResponseBody?): Response? {
		body ?: return null
		return response.newBuilder()
				.body(
						ResponseBody.create(body.contentType(),
								body.bytes())
				).build()
	}

	fun doubleResponse(response: Response, body: ResponseBody?): Pair<Response, Response>? {
		body ?: return null

		val contentType = body.contentType()
		val content = body.bytes()

		val first = response.newBuilder()
				.body(
						ResponseBody.create(contentType,
								content)
				).build()

		val second = response.newBuilder()
				.body(
						ResponseBody.create(contentType,
								content)
				).build()

		return Pair(first, second)
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