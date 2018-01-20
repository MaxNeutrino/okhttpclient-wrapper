package neutrino.project.clientwrapper.request.builder

import neutrino.project.clientwrapper.ProcessorStore
import neutrino.project.clientwrapper.request.executable.AsyncExecutableRequest
import neutrino.project.clientwrapper.request.executable.DefaultAsyncExecutableRequest
import neutrino.project.clientwrapper.request.executable.ExecutableRequest
import neutrino.project.clientwrapper.request.executable.SimpleExecutableRequest
import neutrino.project.clientwrapper.Client
import okhttp3.FormBody
import okhttp3.Request

class SimpleRequestBuilder(
		client: Client,
		baseUrl: String,
		processorStore: ProcessorStore,
		userAgent: String? = null) : AbstractRequestBuilder(client, baseUrl, processorStore, userAgent) {

	override val builder = Request.Builder()
	private var params: Map<String, String>? = null
	private var initUrl = ""

	override fun get(): ExecutableRequest {
		prepareGet()
		return SimpleExecutableRequest(this)
	}

	override fun post(): ExecutableRequest {
		preparePost()
		return SimpleExecutableRequest(this)
	}

	override fun asyncGet(): AsyncExecutableRequest {
		prepareGet()
		return DefaultAsyncExecutableRequest(this)
	}

	override fun asyncPost(): AsyncExecutableRequest {
		preparePost()
		return DefaultAsyncExecutableRequest(this)
	}

	override fun create(url: String, customUrl: String, headers: Map<String, String>?,
						body: Map<String, String>?): RequestBuilder {
		initUrl = if (customUrl.isEmpty()) "$baseUrl$url" else customUrl
		headers?.forEach { k, v -> builder.header(k, v) }
		this.params = body

		return this
	}

	private fun prepareGet() {
		val urlWithParams = if (params != null) {
			params!!.map { "${it.key}=${it.value}" }
					.joinToString("&")
					.let { "$initUrl?$it" }
		} else initUrl

		builder.url(urlWithParams)
		builder.get()
	}

	private fun preparePost() {
		val requestBodyBuilder = FormBody.Builder()
		params?.forEach { requestBodyBuilder.add(it.key, it.value) }
		val requestBody = requestBodyBuilder.build()

		builder.post(requestBody)
		builder.url(initUrl)
	}
}