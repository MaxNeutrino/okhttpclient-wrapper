package neutrino.project.clientwrapper.request.builder

import neutrino.project.clientwrapper.ProcessorStore
import neutrino.project.clientwrapper.Client
import java.io.File

abstract class MultipartRequestBuilder(client: Client,
                                       baseUrl: String,
                                       processorStore: ProcessorStore,
                                       userAgent: String? = null) : AbstractRequestBuilder(client, baseUrl, processorStore, userAgent) {

    abstract fun withFile(name: String = "", body: File): MultipartRequestBuilder
}