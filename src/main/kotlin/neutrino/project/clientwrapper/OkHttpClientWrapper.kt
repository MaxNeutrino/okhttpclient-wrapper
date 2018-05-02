package neutrino.project.clientwrapper

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


class OkHttpClientWrapper(private var baseUrl: String,
						  private val coreClient: OkHttpClient,
						  private val processorStore: ProcessorStore,
						  private val cookieManager: CookieManager?,
						  private val storageProvider: StorageProvider = DefaultStorageProvider(),
						  cookiesFileName: String?) : Client {

	private var cookieHandler: ClientCookieHandler? = null

	private var userAgent = ""

	init {

		if(cookieManager != null) {
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

	override fun get(url: String, customUrl: String?, body: Params?, header: Params?): Call? {
		var request = Request.Builder()
				.url(customUrl ?: "$baseUrl$url")
				.get()

		header?.getParams()?.forEach { request.addHeader(it.first, it.second) }

		if(userAgent.isNotEmpty()) {
			request.addHeader("User-Agent", userAgent)
		}

		processorStore.getRequestProcessors()
				.forEach {
					request = it.process(this, request)
				}

		return coreClient.newCall(request.build())
	}

	override fun post(url: String, customUrl: String?, body: Params, header: Params?): Call? {
		var request = Request.Builder()
				.url(customUrl ?: "$baseUrl$url")

		header?.getParams()?.forEach { request.addHeader(it.first, it.second) }

		if(userAgent.isNotEmpty()) {
			request.addHeader("User-Agent", userAgent)
		}

		val formBody = FormBody.Builder()

		body.getParams().forEach{
			formBody.add(it.first, it.second)
		}

		request.post(formBody.build())

		processorStore.getRequestProcessors()
				.forEach {
					request = it.process(this, request)
				}


		return coreClient.newCall(request.build())
	}

	override fun sendFile(url: String, customUrl: String?, body: Map<String, String>?, header: Map<String, String>?,
						  name: String, file: File): Call? {

		var request = Request.Builder()
				.url(customUrl ?: "$baseUrl$url")

		header?.forEach { t, u -> request.addHeader(t, u) }

		if(userAgent.isNotEmpty()) {
			request.addHeader("User-Agent", userAgent)
		}

		val multipartBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart(name, file.name,
						RequestBody.create(MediaType.parse("multipart/form-data"), file))

		if(body != null) {
			val formBody = FormBody.Builder()

			body.forEach{
				formBody.add(it.key, it.value)
			}

			multipartBody.addPart(formBody.build())
		}

		request.post(multipartBody.build())

		processorStore.getRequestProcessors()
				.forEach {
					request = it.process(this, request)
				}

		return coreClient.newCall(request.build())
	}

	override fun send(request: Request): Call? {
		return coreClient.newCall(request)
	}

	override fun processAndSend(request: Request.Builder): Call? {
		var builder = request
		processorStore.getRequestProcessors()
				.forEach {
					builder = it.process(this, builder)
				}

		return send(builder.build())
	}

	override fun <T: Any> send(request: RequestMethod<T>): T {
		return MethodBuilder<T>(this).build(request)
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
}