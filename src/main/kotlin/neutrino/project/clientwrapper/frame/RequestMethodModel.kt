package neutrino.project.clientwrapper.frame

import neutrino.project.clientwrapper.util.exception.RequestMethodException
import neutrino.project.clientwrapper.util.exception.UrlNotFoundException
import okhttp3.Request
import okhttp3.RequestBody


data class RequestMethodModel(
		val requestBuilder: Request.Builder,
		var requestBody: RequestBody? = null,
		val baseUrl: String = "",
		var apiUrl: String? = null,
		var customUrl: String? = null,
		var requestMethod: RequestMethodName = RequestMethodName.GET) {

	@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
	fun build(): Request.Builder {
		if (apiUrl != null)
			requestBuilder.url("$baseUrl$apiUrl")
		else
			requestBuilder.url(customUrl)

		if (RequestMethodName.POST == requestMethod) {
			requestBody ?: throw RequestMethodException("Can't send post without body")
			requestBuilder.post(requestBody)
		}

		return requestBuilder
	}

	fun setMethod(method: RequestMethod<*>) {
		requestMethod = when(method) {
			is GetMethod -> RequestMethodName.GET
			is PostMethod -> RequestMethodName.POST
			is JsonPostMethod -> RequestMethodName.POST
		}
	}

	fun getUrl(): String {
		return if (apiUrl != null)
			apiUrl!!
		else
			customUrl ?: throw UrlNotFoundException()
	}

	fun setUrl(url: String) {
		when {
			apiUrl != null -> apiUrl = url
			customUrl != null -> customUrl = url
			else -> throw UrlNotFoundException()
		}
	}


}

enum class RequestMethodName {
	GET, POST
}