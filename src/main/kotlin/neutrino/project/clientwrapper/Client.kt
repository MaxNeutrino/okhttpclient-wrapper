package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler


interface Client {

	fun getClientCookieHandler(): ClientCookieHandler

	fun sendGet(url: String): String

	fun sendPost(url: String, body: Map<String, String>): String

	fun newRequestBuilder(): RequestBuilder

	companion object {
		fun createDefault(baseUrl: String): Client {
			return OkHttpClientWrapper(baseUrl)
		}
	}
}