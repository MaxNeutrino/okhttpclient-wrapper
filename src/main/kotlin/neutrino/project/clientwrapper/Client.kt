package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.cookie.ClientCookieHandler
import neutrino.project.clientwrapper.util.exception.BadRequestException
import okhttp3.*
import java.io.File
import java.net.*
import java.util.*
import java.util.concurrent.TimeUnit


class Client {

	val baseUrl: String

	val coreClient: OkHttpClient

	var cookieManager: CookieManager? = null

	val cookieHandler = ClientCookieHandler(this)

	constructor(baseUrl: String) {
		this.baseUrl = baseUrl
		createCookieManager()
		this.coreClient = createDefault()
	}

	constructor(baseUrl: String, coreClient: OkHttpClient) {
		this.baseUrl = baseUrl
		this.coreClient = coreClient
	}

	fun sendGet(url: String): String {
		return newRequestBuilder()
				.url(url)
				.get()
				.executeAndGetBody()
				.orElseThrow { BadRequestException() }
	}

	fun sendPost(url: String, body: Map<String, String>): String {
		return newRequestBuilder()
				.url(url)
				.post(body)
				.executeAndGetBody()
				.orElseThrow { BadRequestException() }
	}

	fun newRequestBuilder(): RequestBuilder {
		return RequestBuilder(Request.Builder())
	}

	inner class RequestBuilder(private val requestBuilder: Request.Builder) {

		fun url(url: String): RequestBuilder {
			requestBuilder.url("$baseUrl$url")
			return this
		}

		fun addHeader(name: String, value: String): RequestBuilder {
			requestBuilder.header(name, value)
			return this
		}

		fun get(): RequestBuilder {
			requestBuilder.get()
			return this
		}

		fun post(params: Map<String, String>): RequestBuilder {
			val requestBodyBuilder = FormBody.Builder()
			params.forEach { requestBodyBuilder.add(it.key, it.value) }
			val requestBody = requestBodyBuilder.build()

			requestBuilder.post(requestBody)
			return this
		}

		fun execute(): Optional<Response> {
			val request: Request? = requestBuilder.build()
			val response = coreClient
					.newCall(request)
					.execute()

			return Optional.ofNullable(response)
		}

		fun executeAndGetBody(): Optional<String> {
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