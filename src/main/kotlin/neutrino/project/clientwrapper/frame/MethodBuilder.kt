package neutrino.project.clientwrapper.frame

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.frame.content.Content
import neutrino.project.clientwrapper.frame.content.JsonContent
import neutrino.project.clientwrapper.frame.content.ReflectiveContentResolver
import neutrino.project.clientwrapper.frame.content.jsonContentResolver
import neutrino.project.clientwrapper.frame.processor.method.IterableMethodProcessor
import neutrino.project.clientwrapper.frame.processor.method.RequestMethodProcessor
import neutrino.project.clientwrapper.frame.processor.method.SingleMethodProcessor
import neutrino.project.clientwrapper.util.exception.IterableModelException
import neutrino.project.clientwrapper.util.exception.ResponseMapperNotFoundException
import neutrino.project.clientwrapper.util.exception.UrlNotFoundException
import okhttp3.Call
import okhttp3.Response
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.function.Supplier
import kotlin.reflect.KClass

class MethodBuilder<T: Any>(private val client: Client, private val type: KClass<out T>) {

	private val resolvers = mapOf(
			"queries" to ::processQuery,
			"path" to ::processPath,
			"body" to ::processBody,
			"headers" to ::processHeaders,
			"json" to ::processJson
	)

	private var responseMapper: KClass<out ResponseMapper<*>>? = null

	@Suppress("UNCHECKED_CAST")
	fun build(method: RequestMethod<T>): Expected<T> {
		method.url ?: throw UrlNotFoundException()

		responseMapper = method.responseMapper ?: getDefaultMapper(type.java)
		method.responseMapper = responseMapper


		val contents = resolvers.map { ReflectiveContentResolver(it.key).resolve(method) }
				.filterNotNull()

		val jsonContent = if (method is JsonPostMethod<T>) {
			jsonContentResolver(method)
		} else null

		val iterableModelContent = findIterableModel(contents)

		val methodProcessor = createMethodProcessor(type, iterableModelContent, contents, jsonContent, method)
		return Expected(methodProcessor)
	}

	private fun <T : Any> createMethodProcessor(clazz: KClass<T>,
												iterableModelContent: Content?,
												contents: Collection<Content>,
												jsonContent: JsonContent?,
												method: RequestMethod<*>): RequestMethodProcessor<T> {
		return if (iterableModelContent != null) {
			IterableMethodProcessor(
					method = method,
					client = client,
					iterableContent = iterableModelContent,
					contents = contents.toList(),
					jsonContent = jsonContent
			)
		} else {
			SingleMethodProcessor(
					method = method,
					client = client,
					contents = contents.toList(),
					jsonContent = jsonContent
			)
		}
	}

	private fun getDefaultMapper(genericType: Class<out Any>): KClass<out ResponseMapper<*>>? {
		return when {
			Response::class.java.isAssignableFrom(genericType) -> EmptyResponseMapper::class
			String::class.java.isAssignableFrom(genericType) -> StringResponseMapper::class
			Call::class.java.isAssignableFrom(genericType) -> null
			else -> throw ResponseMapperNotFoundException()
		}
	}

	private fun findIterableModel(contents: Collection<Content>): Content? {
		val iterableModelContent = contents.filter {
			if (it.model != null) {
				Iterable::class.java.isAssignableFrom(it.model::class.java)
			} else {
				false
			}
		}

		return when {
			iterableModelContent.size > 1 -> throw IterableModelException("Too many iterable models")
			iterableModelContent.size == 1 -> iterableModelContent.first()
			else -> null
		}
	}

	private fun <T : Any> getCompletableFuture(func: () -> T): CompletableFuture<T> {
		val executor = Executors.newSingleThreadExecutor()
		return CompletableFuture.supplyAsync(Supplier { func() }, executor)
	}
}