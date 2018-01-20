package neutrino.project.clientwrapper.request.executable

import neutrino.project.clientwrapper.request.builder.AbstractRequestBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class DefaultAsyncExecutableRequest(private val requestBuilder: AbstractRequestBuilder) : AsyncExecutableRequest {

	private var request: Request?

	init {
		val userAgent = requestBuilder.client.getUserAgent()
		if (userAgent != null)
			requestBuilder.builder.header("User-Agent", userAgent)

		requestBuilder.processorStore.getRequestProcessors()
				.forEach { it.process(requestBuilder.client, requestBuilder.builder) }

		request = requestBuilder.builder.build() ?: null
	}

	override fun withCallback(success: (call: Call?, response: Response) -> Unit,
							  failure: (call: Call?, e: Exception?) -> Unit) {
		if (request != null) {
			requestBuilder.client.coreClient()
					.newCall(request!!)
					.enqueue(object : Callback {

						override fun onFailure(call: Call?, e: IOException?) {
							failure.invoke(call, e)
						}

						override fun onResponse(call: Call?, response: Response?) {
							response ?: onFailure(call, IOException("Unexpected code " + response))
							if (response!!.isSuccessful) {
								success.invoke(call, response)
							} else {
								onFailure(call, IOException("Unexpected code " + response))
							}
						}
					})
		}
	}

	override fun withResponse(success: (response: Response) -> Unit,
							  failure: (e: Exception?) -> Unit) {
		if (request != null) {
			requestBuilder.client.coreClient()
					.newCall(request!!)
					.enqueue(object : Callback {

						override fun onFailure(call: Call?, e: IOException?) {
							failure.invoke(e)
						}

						override fun onResponse(call: Call?, response: Response?) {
							response ?: onFailure(call, IOException("Unexpected code " + response))
							if (response!!.isSuccessful) {
								success.invoke(response)
							} else {
								onFailure(call, IOException("Unexpected code " + response))
							}
						}
					})
		}
	}
}