package neutrino.project.clientwrapper

@Deprecated("use interceptors for check auth status")
interface Response {

	fun body(): String?

	fun code(): Int

	@Deprecated("", replaceWith = ReplaceWith("AuthorizationCheckInterceptor"))
	fun isAuthorized(checkData: String): Response

	@Deprecated("", replaceWith = ReplaceWith("AuthorizationCheckInterceptor"))
	fun isAuthorized(checkFunc: () -> Boolean): Response
}