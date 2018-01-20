package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.global.GlobalClient
import neutrino.project.clientwrapper.request.builder.MultipartRequestBuilder
import neutrino.project.clientwrapper.request.builder.RequestBuilder
import neutrino.project.clientwrapper.request.executable.AsyncExecutableRequest
import neutrino.project.clientwrapper.processor.request.RequestProcessor
import neutrino.project.clientwrapper.processor.response.ResponseProcessor
import neutrino.project.clientwrapper.storage.DefaultStorageProvider
import neutrino.project.clientwrapper.storage.StorageProvider
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import java.io.File


interface Client {

	fun changeBaseUrl(url: String)

	fun getBaseUrl(): String

	fun withUserAgent(agent: String = defaultUserAgent): Client

	fun getClientCookieHandler(): ClientCookieHandler

	fun get(url: String = "", customUrl: String = "", body: Map<String, String>? = null,
			header: Map<String, String>? = null): Response

	fun post(url: String = "", customUrl: String = "", body: Map<String, String>,
			 header: Map<String, String>? = null): Response

	fun sendFile(url: String = "",
				 customUrl: String = "",
				 body: Map<String, String>? = null,
				 header: Map<String, String>? = null,
				 name: String = "",
				 file: File): Response

	fun asyncGet(url: String = "", customUrl: String = "", body: Map<String, String>? = null,
				 header: Map<String, String>? = null): AsyncExecutableRequest

	fun asyncPost(url: String = "", customUrl: String = "", body: Map<String, String>,
				  header: Map<String, String>? = null): AsyncExecutableRequest

	fun asyncSendFile(url: String = "",
					  customUrl: String = "",
					  body: Map<String, String>? = null,
					  header: Map<String, String>? = null,
					  name: String = "",
					  file: File): AsyncExecutableRequest

	fun getProcessorStore(): ProcessorStore

	fun newRequestBuilder(): RequestBuilder

	fun newMultiPartRequestBuilder(): MultipartRequestBuilder

	fun coreClient(): OkHttpClient

	fun getUserAgent(): String?

	fun asGlobal(): GlobalClient {
		GlobalClient.setAsGlobal(this)
		return GlobalClient
	}

	companion object {
		const val defaultUserAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36"

		fun createDefault(baseUrl: String): Client {
			return create(baseUrl = baseUrl, isUnsafe = false)
		}

		fun create(baseUrl: String = "",
				   isUnsafe: Boolean = false,
				   interceptors: List<Interceptor> = listOf(),
				   requestProcessors: List<RequestProcessor> = listOf(),
				   responseProcessors: List<ResponseProcessor> = listOf(),
				   storageProvider: StorageProvider = DefaultStorageProvider(),
				   protocols: List<Protocol> = listOf(Protocol.HTTP_1_1),  // set HTTP_2 with JAVA 9
				   cookiesFilePath: String? = null): Client {

			return OkHttpClientWrapper(
					baseUrl = baseUrl,
					isUnsafe = isUnsafe,
					interceptors = interceptors,
					requestProcessors = requestProcessors,
					responseProcessors = responseProcessors,
					cookiesFileName = cookiesFilePath,
					protocols = protocols,
					storageProvider = storageProvider
			)
		}
	}
}