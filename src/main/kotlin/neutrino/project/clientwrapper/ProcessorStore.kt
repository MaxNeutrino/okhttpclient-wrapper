package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.processor.response.ResponseProcessor
import neutrino.project.clientwrapper.processor.request.RequestProcessor


interface ProcessorStore {

	fun registerRequestProcessor(requestProcessor: RequestProcessor)

	fun removeRequestProcessor(requestProcessor: RequestProcessor)

	fun getRequestProcessors(): List<RequestProcessor>

	fun registerResponseProcessor(responseProcessor: ResponseProcessor)

	fun removeResponseProcessor(responseProcessor: ResponseProcessor)

	fun getResponseProcessors(): List<ResponseProcessor>
}