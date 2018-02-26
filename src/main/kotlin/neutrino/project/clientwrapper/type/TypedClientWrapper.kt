package neutrino.project.clientwrapper.type

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.ProcessorStore
import neutrino.project.clientwrapper.TypedClient
import neutrino.project.clientwrapper.request.builder.MultipartRequestBuilder
import neutrino.project.clientwrapper.request.builder.RequestBuilder
import neutrino.project.clientwrapper.request.executable.AsyncExecutableRequest
import neutrino.project.clientwrapper.request.executable.AsyncExecutable
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.File


open class TypedClientWrapper<T>(protected val baseClient: Client, protected val wrapFunc: (Response?) -> T): TypedClient<T> {

	override fun changeBaseUrl(url: String) = baseClient.changeBaseUrl(url)

	override fun getBaseUrl(): String = baseClient.getBaseUrl()

	override fun withUserAgent(agent: String): Client = baseClient.withUserAgent(agent)

	override fun getClientCookieHandler(): ClientCookieHandler = baseClient.getClientCookieHandler()

	override fun get(url: String, customUrl: String, body: Map<String, String>?, header: Map<String, String>?): T {
		return baseClient.get(url, customUrl, body, header)
				.let { wrapFunc.invoke(it) }
	}

	override fun post(url: String, customUrl: String, body: Map<String, String>, header: Map<String, String>?): T {
		return baseClient.post(url, customUrl, body, header)
				.let { wrapFunc.invoke(it) }
	}

	override fun sendFile(url: String, customUrl: String, body: Map<String, String>?, header: Map<String, String>?,
						  name: String, file: File): T {
		return baseClient.sendFile(url, customUrl, body, header, name, file)
				.let { wrapFunc.invoke(it) }
	}

	override fun asyncGet(url: String, customUrl: String, body: Map<String, String>?,
						  header: Map<String, String>?): AsyncExecutable<T> {
		return baseClient.asyncGet(url, customUrl, body, header)
				.let { CustomAsyncExecutableRequest(it as AsyncExecutableRequest, wrapFunc) }
	}

	override fun asyncPost(url: String, customUrl: String, body: Map<String, String>,
						   header: Map<String, String>?): AsyncExecutable<T> {
		return baseClient.asyncPost(url, customUrl, body, header)
				.let { CustomAsyncExecutableRequest(it as AsyncExecutableRequest, wrapFunc) }
	}

	override fun asyncSendFile(url: String, customUrl: String, body: Map<String, String>?, header: Map<String, String>?,
							   name: String, file: File): AsyncExecutable<T> {
		return baseClient.asyncSendFile(url, customUrl, body, header, name, file)
				.let { CustomAsyncExecutableRequest(it as AsyncExecutableRequest, wrapFunc) }
	}

	override fun getProcessorStore(): ProcessorStore = baseClient.getProcessorStore()


	override fun newRequestBuilder(): RequestBuilder = baseClient.newRequestBuilder()

	override fun newMultiPartRequestBuilder(): MultipartRequestBuilder = baseClient.newMultiPartRequestBuilder()

	override fun coreClient(): OkHttpClient = baseClient.coreClient()

	override fun getUserAgent(): String? = baseClient.getUserAgent()
}