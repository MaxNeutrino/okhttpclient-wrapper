package neutrino.project.clientwrapper.request.builder

import neutrino.project.clientwrapper.request.executable.AsyncExecutableRequest
import neutrino.project.clientwrapper.request.executable.ExecutableRequest


interface RequestBuilder {

	fun get(): ExecutableRequest

	fun post(): ExecutableRequest

	fun asyncGet(): AsyncExecutableRequest

	fun asyncPost(): AsyncExecutableRequest

	fun create(
			url: String = "",
			customUrl: String = "",
			headers: Map<String, String>? = null,
			body: Map<String, String>? = null
	): RequestBuilder
}