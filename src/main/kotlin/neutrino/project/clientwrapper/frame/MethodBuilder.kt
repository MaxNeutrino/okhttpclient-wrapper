package neutrino.project.clientwrapper.frame

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.frame.content.Content
import neutrino.project.clientwrapper.frame.content.ReflectiveContentResolver
import neutrino.project.clientwrapper.frame.processor.method.IterableMethodProcessor
import neutrino.project.clientwrapper.frame.processor.method.RequestMethodProcessor
import neutrino.project.clientwrapper.frame.processor.method.SingleMethodProcessor
import neutrino.project.clientwrapper.util.exception.IterableModelException
import neutrino.project.clientwrapper.util.exception.RequestMethodException
import neutrino.project.clientwrapper.util.exception.ResponseMapperNotFoundException
import neutrino.project.clientwrapper.util.exception.UrlNotFoundException
import neutrino.project.clientwrapper.util.ext.findGenerics
import neutrino.project.clientwrapper.util.ext.generic
import neutrino.project.clientwrapper.util.ext.subOf
import okhttp3.Call
import okhttp3.Response
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class MethodBuilder<T : Any>(private val client: Client) {

	private val resolversName = mapOf(
			"queries" to ::processQuery,
			"path" to ::processPath,
			"body" to ::processBody,
			"headers" to ::processHeaders,
			"json" to ::processJson
	)

	private var responseMapper: KClass<out ResponseMapper<*>>? = null

	@Suppress("UNCHECKED_CAST")
	fun build(method: RequestMethod<T>): T {
		method.url ?: throw UrlNotFoundException()

		val genericType = this.generic
		responseMapper = method.responseMapper ?: getDefaultMapper(genericType.java)

		val contents = resolversName.map { ReflectiveContentResolver(it.key).resolve(method) }
				.filterNotNull()

		val iterableModelContent = findIterableModel(contents)

		return when {
			genericType subOf Expected::class -> {
				val expectedGeneric = genericType.generic
				val methodProcessor = createMethodProcessor(expectedGeneric, iterableModelContent, contents, method)
				genericType.primaryConstructor?.call(methodProcessor)
			}
			genericType subOf Future::class -> {
				val completableGeneric = genericType.generic
				val methodProcessor = createMethodProcessor(completableGeneric, iterableModelContent, contents, method)
				getCompletableFuture { methodProcessor.process() }
			}
			else -> {
				val methodProcessor = createMethodProcessor(genericType, iterableModelContent, contents, method)

				methodProcessor.process()
			}
		} as T
	}

	private fun <T : Any> createMethodProcessor(clazz: KClass<T>,
												iterableModelContent: Content?,
												contents: Collection<Content>,
												method: RequestMethod<*>): RequestMethodProcessor<T> {
		return if (iterableModelContent != null) {
			responseMapper ?: throw RequestMethodException("Can't send iterable request without mapper")
			IterableMethodProcessor(
					method = method,
					client = client,
					iterableContent = iterableModelContent,
					contents = contents.toList()
			)
		} else {
			SingleMethodProcessor(
					method = method,
					client = client,
					contents = contents.toList()
			)
		}
	}

	private fun getDefaultMapper(genericType: Class<*>): KClass<out ResponseMapper<*>>? {
		return when {
			Response::class.java.isAssignableFrom(genericType) -> MockResponseMapper::class
			String::class.java.isAssignableFrom(genericType) -> StringResponseMapper::class
			Future::class.java.isAssignableFrom(genericType) -> getDefaultMapper(genericType.findGenerics().first())
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