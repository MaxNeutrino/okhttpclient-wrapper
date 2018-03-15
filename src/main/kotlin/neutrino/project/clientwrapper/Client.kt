package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.processor.response.ResponseProcessingInterceptor
import neutrino.project.clientwrapper.storage.DefaultStorageProvider
import neutrino.project.clientwrapper.storage.StorageProvider
import neutrino.project.clientwrapper.util.BuildUtil
import neutrino.project.clientwrapper.util.cookie.JavaNetCookieJar
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import okhttp3.Call
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit


interface Client {

	fun changeBaseUrl(url: String)

	fun getBaseUrl(): String

	fun withUserAgent(agent: String = Client.defaultUserAgent): Client

	fun getClientCookieHandler(): ClientCookieHandler?

	fun get(url: String = "", customUrl: String? = null, body: Map<String, String>? = null,
			header: Map<String, String>? = null): Call?

	fun post(url: String = "", customUrl: String? = null, body: Map<String, String>,
			 header: Map<String, String>? = null): Call?

	fun sendFile(url: String = "",
				 customUrl: String? = null,
				 body: Map<String, String>? = null,
				 header: Map<String, String>? = null,
				 name: String = "",
				 file: File): Call?

	fun send(request: Request): Call?

	fun getProcessorStore(): ProcessorStore

	fun coreClient(): OkHttpClient

	fun getUserAgent(): String?

	fun getStorageProvider(): StorageProvider

	companion object {
		const val defaultUserAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36"

		fun createDefault(baseUrl: String): Client {
			val processorStore = DefaultProcessorStore(mutableListOf(), mutableListOf())

			val responseProcessingInterceptor = ResponseProcessingInterceptor(processorStore)

			val cookieManager = CookieManager()
			cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

			val clientBuilder = OkHttpClient.Builder()
					.cache(BuildUtil.getCache(baseUrl))
					.followRedirects(true)
					.connectTimeout(2, TimeUnit.MINUTES)
					.readTimeout(2, TimeUnit.MINUTES)
					.writeTimeout(2, TimeUnit.MINUTES)
					.connectionPool(ConnectionPool(Runtime.getRuntime().availableProcessors() * 2, 5, TimeUnit.MINUTES))
					.addInterceptor(responseProcessingInterceptor)
					.cookieJar(JavaNetCookieJar(cookieManager))

			return create(baseUrl = baseUrl, okHttpClient = clientBuilder.build(), processorStore = processorStore)
		}

		fun create(baseUrl: String,
				   okHttpClient: OkHttpClient,
				   processorStore: ProcessorStore,
				   isAutoSaveCookies: Boolean,
				   storageProvider: StorageProvider = DefaultStorageProvider()): Client {

			val cookieManager = if (isAutoSaveCookies) {
				val cm = CookieManager()
				cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
				cm
			} else null

			val cookiesFileName = if (isAutoSaveCookies) {
				"tmp.cookies"
			} else null

			return create(
					baseUrl = baseUrl,
					okHttpClient = okHttpClient,
					processorStore = processorStore,
					cookieManager = cookieManager,
					cookiesFileName = cookiesFileName
			)
		}

		fun create(baseUrl: String,
				   okHttpClient: OkHttpClient,
				   processorStore: ProcessorStore,
				   cookieManager: CookieManager? = null,
				   storageProvider: StorageProvider = DefaultStorageProvider(),
				   cookiesFileName: String? = null): Client {

			return OkHttpClientWrapper(
					baseUrl = baseUrl,
					coreClient = okHttpClient,
					processorStore = processorStore,
					cookieManager = cookieManager,
					storageProvider = storageProvider,
					cookiesFileName = cookiesFileName
			)
		}
	}
}