package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.frame.Expected
import neutrino.project.clientwrapper.frame.RequestMethod
import neutrino.project.clientwrapper.processor.ProcessorStore
import neutrino.project.clientwrapper.processor.request.RequestProcessor
import neutrino.project.clientwrapper.processor.response.ResponseProcessingInterceptor
import neutrino.project.clientwrapper.processor.response.ResponseProcessor
import neutrino.project.clientwrapper.storage.DefaultStorageProvider
import neutrino.project.clientwrapper.storage.StorageProvider
import neutrino.project.clientwrapper.util.cookie.JavaNetCookieJar
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import okhttp3.*
import java.io.File
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.Proxy
import java.net.ProxySelector
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.reflect.KClass


interface Client {

	fun changeBaseUrl(url: String)

	fun getBaseUrl(): String

	fun withUserAgent(agent: String = Client.defaultUserAgent): Client

	fun getClientCookieHandler(): ClientCookieHandler?

	fun get(url: String = "", customUrl: String? = null, body: Params? = null,
			header: Params? = null): Call?

	fun post(url: String = "", customUrl: String? = null, body: Params,
			 header: Params? = null): Call?

	fun sendFile(url: String = "",
				 customUrl: String? = null,
				 body: Map<String, String>? = null,
				 header: Map<String, String>? = null,
				 name: String = "",
				 file: File): Call?

	fun send(request: Request): Call?

	fun processAndSend(request: Request.Builder): Call?

	fun <T: Any> send(expectedClass: KClass<T>, request: RequestMethod<T>): Expected<T>

	fun getProcessorStore(): ProcessorStore

	fun coreClient(): OkHttpClient

	fun getUserAgent(): String?

	fun getStorageProvider(): StorageProvider

	companion object {
		const val defaultUserAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36"

		fun createSimple(baseUrl: String): Client {
			return Builder()
					.baseUrl(baseUrl)
					.build()
		}
	}

	class Builder {

		private val builder = OkHttpClient.Builder()

		private var baseUrl: String = ""
		private var processorStore = DefaultProcessorStore(mutableListOf(), mutableListOf())
		private var storageProvider: StorageProvider? = null
		private var cookiesFileName: String = "default.cookies"
		private var cookieManager: CookieManager? = null
		private var userAgent: String? = null

		private var isCookieJarSet = false
		private var isCacheSet = false

		fun connectTimeout(timeout: Long, unit: TimeUnit): Builder {
			builder.connectTimeout(timeout, unit)
			return this
		}

		fun readTimeout(timeout: Long, unit: TimeUnit): Builder {
			builder.readTimeout(timeout, unit)
			return this
		}

		fun writeTimeout(timeout: Long, unit: TimeUnit): Builder {
			builder.writeTimeout(timeout, unit)
			return this
		}

		fun pingInterval(interval: Long, unit: TimeUnit): Builder {
			builder.pingInterval(interval, unit)
			return this
		}

		fun proxy(proxy: Proxy?): Builder {
			builder.proxy(proxy)
			return this
		}

		fun proxySelector(proxySelector: ProxySelector): Builder {
			builder.proxySelector(proxySelector)
			return this
		}

		fun cookieJar(cookieJar: CookieJar): Builder {
			isCookieJarSet = true
			builder.cookieJar(cookieJar)
			return this
		}

		fun cache(cache: Cache?): Builder {
			isCacheSet = true
			builder.cache(cache)
			return this
		}

		fun dns(dns: Dns): Builder {
			builder.dns(dns)
			return this
		}

		fun socketFactory(socketFactory: SocketFactory): Builder {
			builder.socketFactory(socketFactory)
			return this
		}

		fun sslSocketFactory(sslSocketFactory: SSLSocketFactory, trustManager: X509TrustManager): Builder {
			builder.sslSocketFactory(sslSocketFactory, trustManager)
			return this
		}

		fun hostnameVerifier(hostnameVerifier: HostnameVerifier): Builder {
			builder.hostnameVerifier(hostnameVerifier)
			return this
		}

		fun certificatePinner(certificatePinner: CertificatePinner): Builder {
			builder.certificatePinner(certificatePinner)
			return this
		}

		fun authenticator(authenticator: Authenticator): Builder {
			builder.authenticator(authenticator)
			return this
		}

		fun proxyAuthenticator(proxyAuthenticator: Authenticator): Builder {
			builder.proxyAuthenticator(proxyAuthenticator)
			return this
		}

		fun connectionPool(connectionPool: ConnectionPool): Builder {
			builder.connectionPool(connectionPool)
			return this
		}

		fun followSslRedirects(followProtocolRedirects: Boolean): Builder {
			builder.followSslRedirects(followProtocolRedirects)
			return this
		}

		fun followRedirects(followRedirects: Boolean): Builder {
			builder.followRedirects(followRedirects)
			return this
		}

		fun retryOnConnectionFailure(retryOnConnectionFailure: Boolean): Builder {
			builder.retryOnConnectionFailure(retryOnConnectionFailure)
			return this
		}

		fun dispatcher(dispatcher: Dispatcher): Builder {
			builder.dispatcher(dispatcher)
			return this
		}

		fun protocols(protocols: MutableList<Protocol>): Builder {
			builder.protocols(protocols)
			return this
		}

		fun connectionSpecs(connectionSpecs: List<ConnectionSpec>): Builder {
			builder.connectionSpecs(connectionSpecs)
			return this
		}

		fun addInterceptor(interceptor: Interceptor): Builder {
			builder.addInterceptor(interceptor)
			return this
		}

		fun addNetworkInterceptor(interceptor: Interceptor): Builder {
			builder.addNetworkInterceptor(interceptor)
			return this
		}

		fun eventListener(eventListener: EventListener): Builder {
			builder.eventListener(eventListener)
			return this
		}

		fun eventListenerFactory(eventListenerFactory: EventListener.Factory): Builder {
			builder.eventListenerFactory(eventListenerFactory)
			return this
		}

		/**
		 * Extended
		 */
		fun allTimeouts(timeout: Long, unit: TimeUnit): Builder {
			connectTimeout(timeout, unit)
			readTimeout(timeout, unit)
			writeTimeout(timeout, unit)
			return this
		}

		fun baseUrl(url: String): Builder {
			baseUrl = url
			return this
		}

		fun addRequestProcessor(processor: RequestProcessor): Builder {
			processorStore.registerRequestProcessor(processor)
			return this
		}

		fun addResponseProcessor(processor: ResponseProcessor): Builder {
			processorStore.registerResponseProcessor(processor)
			return this
		}

		fun storageProvider(storageProvider: StorageProvider): Builder {
			this.storageProvider = storageProvider
			return this
		}

		fun cookiesFileName(fileName: String): Builder {
			cookiesFileName = fileName
			return this
		}

		fun cookieManager(cookieManager: CookieManager): Builder {
			this.cookieManager = cookieManager
			return this
		}

		fun userAgent(agent: String): Builder {
			userAgent = agent
			return this
		}

		fun build(): Client {
			addInterceptor(ResponseProcessingInterceptor(processorStore))

			storageProvider = storageProvider ?: DefaultStorageProvider()
			cookieManager = cookieManager ?: CookieManager().also { it.setCookiePolicy(CookiePolicy.ACCEPT_ALL) }

			if(!isCookieJarSet) {
				cookieJar(JavaNetCookieJar(cookieManager!!))
			}

			if(!isCacheSet) {
				cache(Cache(File(storageProvider!!.cacheDir, getCacheFileName(baseUrl)), 1024))
			}

			val okHttpClient = builder.build()

			return OkHttpClientWrapper(
					baseUrl = baseUrl,
					coreClient = okHttpClient,
					processorStore = processorStore,
					cookieManager = cookieManager,
					storageProvider = storageProvider!!,
					cookiesFileName = cookiesFileName
			).also {
				if(userAgent != null) {
					it.withUserAgent(userAgent!!)
				}
			}
		}

		private fun getCacheFileName(url: String) = url.replace("http://", "")
				.replace("https://", "")
				.replace("www.", "")
				.replace("/", "_")
				.let { "$it.cache" }
	}
}