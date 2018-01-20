package neutrino.project.clientwrapper.processor.request


interface TokenRequestProcessor : RequestProcessor {

	fun setToken(token: String)

	fun getToken(): String

	fun removeToken()
}