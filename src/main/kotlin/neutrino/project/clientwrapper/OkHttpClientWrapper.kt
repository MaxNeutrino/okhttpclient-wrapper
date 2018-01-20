package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.request.builder.RequestBuilder
import neutrino.project.clientwrapper.processor.response.ResponseProcessor
import neutrino.project.clientwrapper.processor.request.RequestProcessor
import neutrino.project.clientwrapper.processor.response.ResponseProcessingInterceptor
import neutrino.project.clientwrapper.request.builder.MultipartRequestBuilder
import neutrino.project.clientwrapper.request.builder.SimpleMultipartRequestBuilder
import neutrino.project.clientwrapper.request.builder.SimpleRequestBuilder
import neutrino.project.clientwrapper.request.executable.AsyncExecutableRequest
import neutrino.project.clientwrapper.storage.StorageProvider
import neutrino.project.clientwrapper.util.cookie.DefaultClientCookieHandler
import neutrino.project.clientwrapper.util.cookie.JavaNetCookieJar
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import neutrino.project.clientwrapper.util.exception.BadRequestException
import okhttp3.*
import java.io.File
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.URI
import java.security.cert.X509Certificate
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class OkHttpClientWrapper(private var baseUrl: String,
						  isUnsafe: Boolean,
						  interceptors: List<Interceptor> = listOf(),
						  requestProcessors: List<RequestProcessor>,
						  responseProcessors: List<ResponseProcessor>,
						  val executors: ExecutorService? = Executors.newWorkStealingPool(),
						  val protocols: List<Protocol>,
						  val storageProvider: StorageProvider,
						  val cookiesFileName: String? = null) : Client {

	val coreClient: OkHttpClient

	var cookieManager: CookieManager? = null

	private val cookieHandler: ClientCookieHandler

	private var userAgent = ""

	private val processorStore: ProcessorStore

	private val responseProcessingInterceptor: ResponseProcessingInterceptor

	init {
		createCookieManager()

		this.processorStore = DefaultProcessorStore(
				requestProcessors.toMutableList(),
				responseProcessors.toMutableList()
		)

		this.responseProcessingInterceptor = ResponseProcessingInterceptor(processorStore)

		this.coreClient = createDefault(isUnsafe, interceptors)
		cookieHandler = DefaultClientCookieHandler(this, cookieManager, storageProvider, cookiesFileName)
	}

	override fun changeBaseUrl(url: String) {
		baseUrl = url
	}

	override fun getBaseUrl(): String = baseUrl

	override fun withUserAgent(agent: String): Client {
		userAgent = agent
		return this
	}

	override fun getClientCookieHandler(): ClientCookieHandler {
		return cookieHandler
	}

	override fun get(url: String, customUrl: String, body: Map<String, String>?,
					 header: Map<String, String>?): Response {
		return newRequestBuilder()
				.create(
						url = url,
						customUrl = customUrl,
						body = body,
						headers = header
				).get()
				.presentExecute()
				.orElseThrow { BadRequestException() }
	}

	override fun post(url: String, customUrl: String, body: Map<String, String>,
					  header: Map<String, String>?): Response {
		return newRequestBuilder()
				.create(
						url = url,
						customUrl = customUrl,
						body = body,
						headers = header
				).post()
				.presentExecute()
				.orElseThrow { BadRequestException() }
	}

	override fun sendFile(url: String, customUrl: String, body: Map<String, String>?, header: Map<String, String>?,
						  name: String, file: File): Response {
		return (newMultiPartRequestBuilder()
				.create(
						url = url,
						customUrl = customUrl,
						body = body,
						headers = header
				) as MultipartRequestBuilder)
				.withFile(name, file)
				.post()
				.presentExecute()
				.orElseThrow { BadRequestException() }
	}

	override fun asyncGet(url: String, customUrl: String, body: Map<String, String>?,
						  header: Map<String, String>?): AsyncExecutableRequest {
		return newRequestBuilder()
				.create(
						url = url,
						customUrl = customUrl,
						body = body,
						headers = header
				).asyncGet()
	}

	override fun asyncPost(url: String, customUrl: String, body: Map<String, String>,
						   header: Map<String, String>?): AsyncExecutableRequest {
		return newRequestBuilder()
				.create(
						url = url,
						customUrl = customUrl,
						body = body,
						headers = header
				).asyncPost()
	}

	override fun asyncSendFile(url: String, customUrl: String, body: Map<String, String>?, header: Map<String, String>?,
							   name: String, file: File): AsyncExecutableRequest {
		return (newMultiPartRequestBuilder()
				.create(
						url = url,
						customUrl = customUrl,
						body = body,
						headers = header
				) as MultipartRequestBuilder)
				.withFile(name, file)
				.asyncPost()
	}

	override fun getProcessorStore(): ProcessorStore = processorStore

	override fun newRequestBuilder(): RequestBuilder {
		return SimpleRequestBuilder(
				client = this,
				baseUrl = baseUrl,
				processorStore = processorStore,
				userAgent = userAgent
		)
	}

	override fun newMultiPartRequestBuilder(): MultipartRequestBuilder {
		return SimpleMultipartRequestBuilder(this, baseUrl,
				processorStore, userAgent)
	}

	override fun coreClient(): OkHttpClient {
		return coreClient
	}

	override fun getUserAgent(): String? {
		return userAgent
	}

	private fun createDefault(isUnsafe: Boolean, interceptors: List<Interceptor>): OkHttpClient {
		val clientBuilder = OkHttpClient.Builder()
				.cache(getCache(baseUrl))
				.followRedirects(true)
				.connectTimeout(2, TimeUnit.MINUTES)
				.readTimeout(2, TimeUnit.MINUTES)
				.writeTimeout(2, TimeUnit.MINUTES)
				.connectionPool(ConnectionPool(15, 5, TimeUnit.MINUTES))
				.addInterceptor(responseProcessingInterceptor)
				.protocols(protocols)

		if (cookieManager != null)
			clientBuilder.cookieJar(JavaNetCookieJar(cookieManager!!))

		if (executors != null)
			clientBuilder.dispatcher(Dispatcher(executors))

		if (isUnsafe)
			clientBuilder.sslSocketFactory(createUnsafeSSL())

		if (interceptors.isNotEmpty()) {
			interceptors.forEach { clientBuilder.addInterceptor(it) }
		}

		return clientBuilder.build()
	}

	private fun createUnsafeSSL(): SSLSocketFactory {
		val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

			override fun getAcceptedIssuers(): Array<X509Certificate> {
				return emptyArray()
			}

			override fun checkClientTrusted(certs: Array<java.security.cert.X509Certificate>, authType: String) {
				//No need to implement.
			}

			override fun checkServerTrusted(certs: Array<java.security.cert.X509Certificate>, authType: String) {
				//No need to implement.
			}
		})
		val sc = SSLContext.getInstance("SSL")
		sc.init(null, trustAllCerts, java.security.SecureRandom())

		return sc.socketFactory
	}

	private fun getCache(child: String): Cache {
		val cacheDir = File(System.getProperty("java.io.tmpdir"), child)
		return Cache(cacheDir, 1024)
	}

	private fun createCookieManager(): CookieManager {

		val cookieManager = CookieManager()
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

		if (cookiesFileName != null) {
			cookieHandler.restoreCookies(cookiesFileName).forEach {
				cookieManager.cookieStore.add(URI.create(baseUrl), it)
			}
		}

		this.cookieManager = cookieManager
		return cookieManager
	}
}