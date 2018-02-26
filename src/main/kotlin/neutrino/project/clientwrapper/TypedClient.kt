package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.request.builder.MultipartRequestBuilder
import neutrino.project.clientwrapper.request.builder.RequestBuilder
import neutrino.project.clientwrapper.request.executable.AsyncExecutable
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import okhttp3.OkHttpClient
import java.io.File


interface TypedClient<R> {

	fun changeBaseUrl(url: String)

	fun getBaseUrl(): String

	fun withUserAgent(agent: String = Client.defaultUserAgent): Client

	fun getClientCookieHandler(): ClientCookieHandler

	fun get(url: String = "", customUrl: String = "", body: Map<String, String>? = null,
			header: Map<String, String>? = null): R

	fun post(url: String = "", customUrl: String = "", body: Map<String, String>,
			 header: Map<String, String>? = null): R

	fun sendFile(url: String = "",
				 customUrl: String = "",
				 body: Map<String, String>? = null,
				 header: Map<String, String>? = null,
				 name: String = "",
				 file: File): R

	fun asyncGet(url: String = "", customUrl: String = "", body: Map<String, String>? = null,
				 header: Map<String, String>? = null): AsyncExecutable<R>

	fun asyncPost(url: String = "", customUrl: String = "", body: Map<String, String>,
				  header: Map<String, String>? = null): AsyncExecutable<R>

	fun asyncSendFile(url: String = "",
					  customUrl: String = "",
					  body: Map<String, String>? = null,
					  header: Map<String, String>? = null,
					  name: String = "",
					  file: File): AsyncExecutable<R>

	fun getProcessorStore(): ProcessorStore

	fun newRequestBuilder(): RequestBuilder

	fun newMultiPartRequestBuilder(): MultipartRequestBuilder

	fun coreClient(): OkHttpClient

	fun getUserAgent(): String?
}