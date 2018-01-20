package neutrino.project.clientwrapper.request.builder

import neutrino.project.clientwrapper.ProcessorStore
import neutrino.project.clientwrapper.Client
import okhttp3.Request

abstract class AbstractRequestBuilder(
        val client: Client,
        val baseUrl: String,
        val processorStore: ProcessorStore,
        val userAgent: String? = null) : RequestBuilder {

    abstract val builder: Request.Builder
}