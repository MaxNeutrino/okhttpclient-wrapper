package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.util.cookie.DefaultClientCookieHandler
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import neutrino.project.clientwrapper.util.exception.BadRequestException
import okhttp3.*
import java.io.File
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*
import java.util.concurrent.TimeUnit


class OkHttpClientWrapper : Client {

	val baseUrl: String

	val coreClient: OkHttpClient

	var cookieManager: CookieManager? = null

	val cookieHandler = DefaultClientCookieHandler(this)

	constructor(baseUrl: String) {
		this.baseUrl = baseUrl
		createCookieManager()
		this.coreClient = createDefault()
	}

	constructor(baseUrl: String, coreClient: OkHttpClient) {
		this.baseUrl = baseUrl
		this.coreClient = coreClient
	}

	override fun getClientCookieHandler(): ClientCookieHandler {
		return cookieHandler
	}

	override fun sendGet(url: String): String {
		return newRequestBuilder()
				.url(url)
				.get()
				.executeAndGetBody()
				.orElseThrow { BadRequestException() }
	}

	override fun sendPost(url: String, body: Map<String, String>): String {
		return newRequestBuilder()
				.url(url)
				.post(body)
				.executeAndGetBody()
				.orElseThrow { BadRequestException() }
	}

	override fun newRequestBuilder(): RequestBuilder {
		return OkHttpRequestBuilder(Request.Builder())
	}

	inner class OkHttpRequestBuilder(private val requestBuilder: Request.Builder) : RequestBuilder {

		override fun url(url: String): OkHttpRequestBuilder {
			requestBuilder.url("$baseUrl$url")
			return this
		}

		override fun addHeader(name: String, value: String): OkHttpRequestBuilder {
			requestBuilder.header(name, value)
			return this
		}

		override fun get(): OkHttpRequestBuilder {
			requestBuilder.get()
			return this
		}

		override fun post(params: Map<String, String>): OkHttpRequestBuilder {
			val requestBodyBuilder = FormBody.Builder()
			params.forEach { requestBodyBuilder.add(it.key, it.value) }
			val requestBody = requestBodyBuilder.build()

			requestBuilder.post(requestBody)
			return this
		}

		override fun execute(): Optional<Response> {
			val request: Request? = requestBuilder.build()
			val response = coreClient
					.newCall(request)
					.execute()

			return Optional.ofNullable(response)
		}

		override fun executeAndGetBody(): Optional<String> {
			val request: Request? = requestBuilder.build()
			val response = coreClient.newCall(request).execute()
			val body = response.body()?.string()
			response.close()

			return Optional.ofNullable(body)
		}
	}

	private fun createDefault(): OkHttpClient {
		return OkHttpClient.Builder()
				.cookieJar(JavaNetCookieJar(cookieManager))
				.cache(getCache("victoria"))
				.followRedirects(true)
				.connectTimeout(2, TimeUnit.MINUTES)
				.readTimeout(2, TimeUnit.MINUTES)
				.writeTimeout(2, TimeUnit.MINUTES)
				.connectionPool(ConnectionPool(15, 5, TimeUnit.MINUTES))
				.build()
	}

	private fun getCache(child: String): Cache {
		val cacheDir = File(System.getProperty("java.io.tmpdir"), child)
		return Cache(cacheDir, 1024)
	}

	private fun createCookieManager(): CookieManager {

		val cookieManager = CookieManager()
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

		this.cookieManager = cookieManager
		return cookieManager
	}
}