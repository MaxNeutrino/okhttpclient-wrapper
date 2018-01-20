package neutrino.project.clientwrapper.global

import neutrino.project.clientwrapper.ProcessorStore
import neutrino.project.clientwrapper.request.builder.MultipartRequestBuilder
import neutrino.project.clientwrapper.request.builder.RequestBuilder
import neutrino.project.clientwrapper.request.executable.AsyncExecutableRequest
import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.File


object GlobalClient : Client {

	private var client: Client? = null

	@Throws(UnsupportedOperationException::class)
	override fun changeBaseUrl(url: String) {
		throw UnsupportedOperationException()
	}

	override fun getBaseUrl(): String = client?.getBaseUrl() ?: throw IllegalStateException("Not set client as global")

	override fun withUserAgent(agent: String): Client {
		client?.withUserAgent(agent)
		return this
	}

	override fun getClientCookieHandler(): ClientCookieHandler {
		return client?.getClientCookieHandler() ?: throw IllegalStateException("Not set client as global")
	}

	override fun get(url: String, customUrl: String, body: Map<String, String>?, header: Map<String, String>?): Response {
		return client?.get(url, customUrl, body, header) ?: throw IllegalStateException("Not set client as global")
	}

	override fun post(url: String, customUrl: String, body: Map<String, String>, header: Map<String, String>?): Response {
		return client?.post(url, customUrl, body, header) ?: throw IllegalStateException("Not set client as global")
	}

	override fun sendFile(url: String, customUrl: String, body: Map<String, String>?, header: Map<String, String>?,
						  name: String, file: File): Response {
		return client?.sendFile(url, customUrl, body, header, name, file) ?: throw IllegalStateException(
				"Not set client as global")
	}

	override fun asyncGet(url: String, customUrl: String, body: Map<String, String>?,
						  header: Map<String, String>?): AsyncExecutableRequest {
		return client?.asyncGet(url, customUrl, body, header) ?: throw IllegalStateException("Not set client as global")
	}

	override fun asyncPost(url: String, customUrl: String, body: Map<String, String>,
						   header: Map<String, String>?): AsyncExecutableRequest {
		return client?.asyncPost(url, customUrl, body, header) ?: throw IllegalStateException(
				"Not set client as global")
	}

	override fun asyncSendFile(url: String, customUrl: String, body: Map<String, String>?, header: Map<String, String>?,
							   name: String, file: File): AsyncExecutableRequest {
		return client?.asyncSendFile(url, customUrl, body, header, name, file) ?: throw IllegalStateException(
				"Not set client as global")
	}

	override fun newMultiPartRequestBuilder(): MultipartRequestBuilder {
		return client?.newMultiPartRequestBuilder() ?: throw IllegalStateException("Not set client as global")
	}

	override fun coreClient(): OkHttpClient {
		return client?.coreClient() ?: throw IllegalStateException("Not set client as global")
	}

	override fun getProcessorStore(): ProcessorStore {
		return client?.getProcessorStore() ?: throw IllegalStateException("Not set global client")
	}

	override fun newRequestBuilder(): RequestBuilder {
		return client?.newRequestBuilder() ?: throw IllegalStateException("Not set global client")
	}

	override fun asGlobal(): GlobalClient {
		throw UnsupportedOperationException()
	}

	override fun getUserAgent(): String? {
		return client?.getUserAgent()
	}

	fun setAsGlobal(client: Client) {
		GlobalClient.client = client
	}
}