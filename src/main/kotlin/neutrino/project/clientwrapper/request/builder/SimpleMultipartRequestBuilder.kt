package neutrino.project.clientwrapper.request.builder

import neutrino.project.clientwrapper.ProcessorStore
import neutrino.project.clientwrapper.request.executable.AsyncExecutableRequest
import neutrino.project.clientwrapper.request.executable.DefaultAsyncExecutableRequest
import neutrino.project.clientwrapper.request.executable.ExecutableRequest
import neutrino.project.clientwrapper.request.executable.SimpleExecutableRequest
import neutrino.project.clientwrapper.Client
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File

class SimpleMultipartRequestBuilder(client: Client,
                                    baseUrl: String,
                                    processorStore: ProcessorStore,
                                    userAgent: String? = null) : MultipartRequestBuilder(client, baseUrl, processorStore, userAgent) {

    override val builder = Request.Builder()
    private var multipartFormBody: MultipartBody? = null

    override fun get(): ExecutableRequest {
        throw UnsupportedOperationException()
    }

    override fun post(): ExecutableRequest {
        multipartFormBody ?: throw IllegalStateException("Cannot send multipart request without file")
        builder.post(multipartFormBody!!)
        return SimpleExecutableRequest(this)
    }

    override fun asyncGet(): AsyncExecutableRequest {
        throw UnsupportedOperationException()
    }

    override fun asyncPost(): AsyncExecutableRequest {
        multipartFormBody ?: throw IllegalStateException("Cannot send multipart request without file")
        builder.post(multipartFormBody!!)
        return DefaultAsyncExecutableRequest(this)
    }

    override fun create(
			url: String,
			customUrl: String,
			headers: Map<String, String>?,
			body: Map<String, String>?): MultipartRequestBuilder {

        val initUrl = if (customUrl.isEmpty()) "$baseUrl$url" else customUrl

        val urlWithParams = if (body != null) {
            body.map { "${it.key}=${it.value}" }
                    .joinToString("&")
                    .let { "$initUrl?$it" }
        } else initUrl

        builder.url(urlWithParams)
        headers?.forEach { k, v -> builder.header(k, v) }

        return this
    }

    override fun withFile(name: String, body: File): MultipartRequestBuilder {
        multipartFormBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(name, body.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), body))
                .build()

        return this
    }
}