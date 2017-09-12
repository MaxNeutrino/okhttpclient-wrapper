package neutrino.project.clientwrapper.cookie

import neutrino.project.clientwrapper.Client
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.net.CookieStore
import java.net.HttpCookie


class ClientCookieHandler(private val client: Client) {

	private var domain: String? = null

	fun addCookie(cookie: HttpCookie) {
		addCookie(cookie.name, cookie.value)
	}

	fun addCookie(cookies: List<HttpCookie>) {
		val clientCookies = cookies.map {
			return@map Cookie.Builder()
					.name(it.name)
					.value(it.value)
					.domain(getDomain())
					.build()
		}

		addCookieToClient(clientCookies.toMutableList())
	}

	fun addCookie(name: String, value: String) {
		val cookie = Cookie.Builder()
				.name(name)
				.value(value)
				.domain(getDomain())
				.build()

		addCookie(cookie)
	}

	fun addCookie(cookie: Cookie) {
		addCookieToClient(mutableListOf(cookie))
	}

	fun addCookieToClient(cookies: MutableList<Cookie>) {
		client.coreClient
				.cookieJar()
				.saveFromResponse(HttpUrl.parse(client.baseUrl), cookies)
	}

	fun getCookies(): List<HttpCookie> {
		return client.cookieManager?.cookieStore?.cookies ?: listOf()
	}

	fun getCookieStore(): CookieStore? {
		return client.cookieManager?.cookieStore
	}


	private fun getDomain(): String? {
		if (domain == null) {
			domain = client.baseUrl
					.replace("http://", "")
					.replace("https://", "")
		}

		return domain
	}

}