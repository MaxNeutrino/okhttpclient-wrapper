package neutrino.project.clientwrapper.util.cookie

import neutrino.project.clientwrapper.OkHttpClientWrapper
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.net.CookieStore
import java.net.HttpCookie


class DefaultClientCookieHandler(private val client: OkHttpClientWrapper) : ClientCookieHandler {

	override fun addCookie(cookie: HttpCookie) {
		addCookie(cookie.name, cookie.value)
	}

	override fun addCookie(cookies: List<HttpCookie>) {
		val clientCookies = cookies.map {
			return@map Cookie.Builder()
					.name(it.name)
					.value(it.value)
					.domain(getSafeDomain(it.domain))
					.expiresAt(it.maxAge)
					.path(it.path)
					.build()
		}

		addCookieToClient(clientCookies.toMutableList())
	}

	override fun addCookie(name: String, value: String) {
		val cookie = Cookie.Builder()
				.name(name)
				.value(value)
				.domain(getSafeDomain(null))
				.build()

		addCookie(cookie)
	}

	override fun addCookie(cookie: Cookie) {
		addCookieToClient(mutableListOf(cookie))
	}

	override fun addCookieToClient(cookies: MutableList<Cookie>) {
		client.coreClient
				.cookieJar()
				.saveFromResponse(HttpUrl.parse(client.baseUrl)!!, cookies)
	}

	override fun getCookies(): List<HttpCookie> {
		return client.cookieManager?.cookieStore?.cookies ?: listOf()
	}

	override fun getCookieStore(): CookieStore? {
		return client.cookieManager?.cookieStore
	}


	private fun getSafeDomain(domain: String?): String {
		return if (domain == null) {
			var myDomain = client.baseUrl
					.replace("http://", "")
					.replace("https://", "")

			if (myDomain.contains("/")) {
				myDomain = myDomain.substring(0, myDomain.indexOf("/"))
			}
			myDomain
		} else {
			domain
		}
	}

}