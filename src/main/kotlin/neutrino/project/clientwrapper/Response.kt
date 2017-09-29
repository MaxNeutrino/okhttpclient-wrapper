package neutrino.project.clientwrapper


interface Response {

	fun body(): String?

	fun code(): Int

	fun isAuthorized(checkData: String): Response

	fun isAuthorized(checkFunc: () -> Boolean): Response
}