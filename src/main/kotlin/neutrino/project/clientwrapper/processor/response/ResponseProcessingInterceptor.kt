package neutrino.project.clientwrapper.processor.response

import neutrino.project.clientwrapper.processor.ProcessorStore
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.nio.charset.Charset


class ResponseProcessingInterceptor(private val processorStore: ProcessorStore) : Interceptor {

	override fun intercept(chain: Interceptor.Chain?): Response? {
		chain ?: return null

		val request = chain.request() ?: return null
		val response = chain.proceed(request) ?: return null
		val body = response.body()

		val source = body?.source()
		source?.request(Long.MAX_VALUE) // Buffer the entire body.
		val buffer = source?.buffer()
		val stringBody = buffer?.clone()?.readString(Charset.forName("UTF-8")).toString()

		val responseProcessors = processorStore.getResponseProcessors()
		if (responseProcessors.isEmpty())
			return response

		responseProcessors
				.forEach { processor ->
					val parsableResponse = response.newBuilder()
							.body(ResponseBody.create(
									response.body()?.contentType(),
									stringBody
							))
							.build()

					processor.process(parsableResponse)
				}
		return response
	}
}