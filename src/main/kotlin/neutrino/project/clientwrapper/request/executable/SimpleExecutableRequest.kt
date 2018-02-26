package neutrino.project.clientwrapper.request.executable

import neutrino.project.clientwrapper.request.builder.AbstractRequestBuilder
import neutrino.project.clientwrapper.util.stringBody
import okhttp3.Request
import okhttp3.Response
import java.util.*

class SimpleExecutableRequest(private val requestBuilder: AbstractRequestBuilder) : ExecutableRequest {

	private var request: Request? = requestBuilder.build()

	override fun presentExecute(): Optional<Response> {
		return execute().let {
			Optional.ofNullable(it)
		}
	}

	override fun execute(): Response? {
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