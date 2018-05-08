package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.frame.Expected
import neutrino.project.clientwrapper.frame.MethodBuilder
import neutrino.project.clientwrapper.frame.RequestMethod
import neutrino.project.clientwrapper.processor.ProcessorStore
import neutrino.project.clientwrapper.storage.DefaultStorageProvider
import neutrino.project.clientwrapper.storage.StorageProvider
import neutrino.project.clientwrapper.util.cookie.DefaultClientCookieHandler
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import okhttp3.*
import java.io.File
import java.net.CookieManager
import java.net.URI
import kotlin.reflect.KClass


class OkHttpClientWrapper(private var baseUrl: String,
						  private val coreClient: OkHttpClient,
						  private val processorStore: ProcessorStore,
						  private val cookieManager: CookieManager?,
						  private val storageProvider: StorageProvider = DefaultStorageProvider(),
						  cookiesFileName: String?) : Client {

	private var cookieHandler: ClientCookieHandler? = null

	private val jsonMediaType = MediaType.parse("application/json; charset=utf-8")

	private var userAgent = ""

	init {

		if (cookieManager != null) {
			if (cookieHandler == null)
				cookieHandler = DefaultClientCookieHandler(this, cookieManager, storageProvider, cookiesFileName)


			if (cookiesFileName != null) {
				cookieHandler!!.restoreCookies(cookiesFileName).forEach {
					cookieManager.cookieStore.add(URI.create(baseUrl), it)
				}
			}
		}
	}

	override fun changeBaseUrl(url: String) {
		baseUrl = url
	}

	override fun getBaseUrl(): String = baseUrl

	override fun withUserAgent(agent: String): Client {
		userAgent = agent
		return this
	}

	override fun getClientCookieHandler(): ClientCookieHandler? {
		return cookieHandler
	}

	override fun get(url: String, customUrl: String?, query: Params?, header: Params?): Call {
		val request = createRequestWithoutBody(url, customUrl, query, header) { it.get() }

		return coreClient.newCall(request)
	}

	override fun post(url: String, customUrl: String?, body: Params, header: Params?): Call {
		val request = createRequestWithBody(url, customUrl, body, header) { builder, body ->
			builder.post(body)
		}
		return coreClient.newCall(request)
	}

	override fun put(url: String, customUrl: String?, body: Params, header: Params?): Call {
		val request = createRequestWithBody(url, customUrl, body, header) { builder, body ->
			builder.put(body)
		}
		return coreClient.newCall(request)
	}

	override fun delete(url: String, customUrl: String?, query: Params, header: Params?): Call {
		val request = createRequestWithoutBody(url, customUrl, query, header) { it.delete() }
		return coreClient.newCall(request)
	}

	override fun jsonPost(url: String, customUrl: String?, json: String, header: Params?): Call {
		val request = createJsonRequest(url, customUrl, json, header) { builder, requestBody ->
			builder.post(requestBody)
		}
		return coreClient.newCall(request)
	}

	override fun jsonPut(url: String, customUrl: String?, json: String, header: Params?): Call {
		val request = createJsonRequest(url, customUrl, json, header) { builder, requestBody ->
			builder.put(requestBody)
		}
		return coreClient.newCall(request)
	}

	override fun send(request: Request): Call {
		return coreClient.newCall(request)
	}

	override fun processAndSend(request: Request.Builder): Call {
		var builder = request
		processorStore.getRequestProcessors()
				.forEach {
					builder = it.process(this, builder)
				}

		return send(builder.build())
	}

	override fun <T : Any> send(expectedClass: KClass<T>, request: RequestMethod<T>): Expected<T> {
		return MethodBuilder(this, expectedClass).build(request)
	}

	override fun getProcessorStore(): ProcessorStore = processorStore

	override fun coreClient(): OkHttpClient {
		return coreClient
	}

	override fun getUserAgent(): String? {
		return userAgent
	}

	override fun getStorageProvider(): StorageProvider {
		return storageProvider
	}

	private fun createRequestWithoutBody(url: String,
										 customUrl: String?,
										 query: Params?,
										 header: Params?,
										 methodInvoker: (Request.Builder) -> Unit): Request {

		val query = query?.joinToString("&") { "${it.first}=${it.second}" }

		val url = if (query != null) {
			val base = customUrl ?: "$baseUrl$url"
			"$base?$query"
		} else {
			customUrl ?: "$baseUrl$url"
		}

		var request = Request.Builder()
				.url(url)

		methodInvoker(request)

		header?.getParams()?.forEach { request.addHeader(it.first, it.second) }

		if (userAgent.isNotEmpty()) {
			request.addHeader("User-Agent", userAgent)
		}

		processorStore.getRequestProcessors()
				.forEach {
					request = it.process(this, request)
				}

		return request.build()
	}

	private fun createRequestWithBody(url: String,
									  customUrl: String?,
									  body: Params,
									  header: Params?,
									  methodInvoker: (Request.Builder, FormBody) -> Unit): Request {

		var request = Request.Builder()
				.url(customUrl ?: "$baseUrl$url")

		header?.getParams()?.forEach { request.addHeader(it.first, it.second) }

		if (userAgent.isNotEmpty()) {
			request.addHeader("User-Agent", userAgent)
		}

		val formBody = FormBody.Builder()

		body.getParams().forEach {
			formBody.add(it.first, it.second)
		}

		methodInvoker(request, formBody.build())

		processorStore.getRequestProcessors()
				.forEach {
					request = it.process(this, request)
				}

		return request.build()
	}

	private fun createJsonRequest(url: String,
							customUrl: String?,
							json: String,
							header: Params?,
							methodInvoker: (Request.Builder, RequestBody) -> Unit): Request {
		var request = Request.Builder()
				.url(customUrl ?: "$baseUrl$url")

		header?.getParams()?.forEach { request.addHeader(it.first, it.second) }

		if (userAgent.isNotEmpty()) {
			request.addHeader("User-Agent", userAgent)
		}

		processorStore.getRequestProcessors()
				.forEach {
					request = it.process(this, request)
				}

		val requestBody = RequestBody.create(jsonMediaType, json)

		methodInvoker(request, requestBody)
		return request.build()
	}
}