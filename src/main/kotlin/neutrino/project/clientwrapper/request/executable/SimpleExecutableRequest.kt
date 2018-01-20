package neutrino.project.clientwrapper.request.executable

import neutrino.project.clientwrapper.request.builder.AbstractRequestBuilder
import neutrino.project.clientwrapper.util.stringBody
import okhttp3.Request
import okhttp3.Response
import java.util.*

class SimpleExecutableRequest(private val requestBuilder: AbstractRequestBuilder) : ExecutableRequest {

	private var request: Request? = null

	override fun presentExecute(): Optional<Response> {
		return execute().let {
			Optional.ofNullable(it)
		}
	}

	override fun execute(): Response? {
		val userAgent = requestBuilder.client.getUserAgent()
		if (userAgent != null && userAgent.isNotEmpty())
			requestBuilder.builder.header("User-Agent", userAgent)

		requestBuilder.processorStore.getRequestProcessors()
				.forEach { it.process(requestBuilder.client, requestBuilder.builder) }

		request = requestBuilder.builder.build() ?: return null

		return requestBuilder
				.client
				.coreClient()
				.newCall(request!!)
				.execute()
	}

	override fun executeAndGetBody(): Optional<String> {
		val response = execute()
		val body = response?.stringBody()

		return Optional.ofNullable(body)
	}
}