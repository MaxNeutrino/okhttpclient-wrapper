package neutrino.project.clientwrapper.frame

import okhttp3.Response

/**
 * Need to implement if method return complex object which not Response, Call or String.
 * Also with CompletableFuture with Response, Call or String, generic types in.
 * @see Response
 * @see okhttp3.Call
 */
interface ResponseMapper<out T> {
	fun map(response: Response): T
}

class StringResponseMapper : ResponseMapper<String> {
	override fun map(response: Response): String {
		return response.body()?.string() ?: ""
	}
}

class EmptyResponseMapper : ResponseMapper<Response> {
	override fun map(response: Response): Response {
		return response
	}
}