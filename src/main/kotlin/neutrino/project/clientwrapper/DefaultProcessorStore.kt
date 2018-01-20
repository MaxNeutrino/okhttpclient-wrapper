package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.processor.response.ResponseProcessor
import neutrino.project.clientwrapper.processor.request.RequestProcessor


class DefaultProcessorStore(private val requestProcessors: MutableList<RequestProcessor>,
							private val responseProcessors: MutableList<ResponseProcessor>) : ProcessorStore {

	override fun registerRequestProcessor(requestProcessor: RequestProcessor) {
		requestProcessors.add(requestProcessor)
	}

	override fun removeRequestProcessor(requestProcessor: RequestProcessor) {
		requestProcessors.remove(requestProcessor)
	}

	override fun getRequestProcessors(): List<RequestProcessor> = requestProcessors

	override fun registerResponseProcessor(responseProcessor: ResponseProcessor) {
		responseProcessors.add(responseProcessor)
	}

	override fun removeResponseProcessor(responseProcessor: ResponseProcessor) {
		responseProcessors.remove(responseProcessor)
	}

	override fun getResponseProcessors(): List<ResponseProcessor> = responseProcessors
}