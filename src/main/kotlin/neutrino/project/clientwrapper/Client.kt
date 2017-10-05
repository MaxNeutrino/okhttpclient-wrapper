package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import okhttp3.Interceptor


interface Client {

	fun getClientCookieHandler(): ClientCookieHandler

	fun get(url: String): String

	fun post(url: String, body: Map<String, String>): String

	@Deprecated("use #get instead")
	fun sendGet(url: String): Response

	@Deprecated("use #post instead")
	fun sendPost(url: String, body: Map<String, String>): Response

	fun newRequestBuilder(): RequestBuilder

	companion object {
		fun createDefault(baseUrl: String): Client {
			return OkHttpClientWrapper(baseUrl, false)
		}

		fun createUnsafe(baseUrl: String): Client {
			return OkHttpClientWrapper(baseUrl, true)
		}

		fun createWithInterceptors(baseUrl: String, isUnsafe: Boolean, interceptors: List<Interceptor>): Client {
			return OkHttpClientWrapper(baseUrl, isUnsafe, interceptors)
		}

		fun newBuilder() = Builder()
	}

	class Builder internal constructor() {
		private var baseUrl: String? = null
		private var isUnsafe = false
		private val interceptors: MutableList<Interceptor> = mutableListOf()
		private var cookiesFilePath: String? = null

		fun baseUrl(baseUrl: String): Builder {
			this.baseUrl = baseUrl
			return this
		}

		fun makeUnsafe(): Builder {
			isUnsafe = true
			return this
		}

		fun addInterceptor(interceptor: Interceptor): Builder {
			interceptors.add(interceptor)
			return this
		}

		fun addInterceptors(interceptors: List<Interceptor>): Builder {
			this.interceptors.addAll(interceptors)
			return this
		}

		fun withRestoredCookie(adminPanelName: String): Builder {
			cookiesFilePath = adminPanelName
			return this
		}

		fun build(): Client {
			return OkHttpClientWrapper(baseUrl!!, isUnsafe, interceptors, cookiesFilePath)
		}
	}
}