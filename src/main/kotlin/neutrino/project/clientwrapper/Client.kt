package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler


interface Client {

	fun getClientCookieHandler(): ClientCookieHandler

	fun sendGet(url: String): Response

	fun sendPost(url: String, body: Map<String, String>): Response

	fun newRequestBuilder(): RequestBuilder

	companion object {
		fun createDefault(baseUrl: String): Client {
			return OkHttpClientWrapper(baseUrl, false)
		}

		fun createUnsafe(baseUrl: String): Client {
			return OkHttpClientWrapper(baseUrl, true)
		}
	}
}