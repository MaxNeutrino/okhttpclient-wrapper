package neutrino.project.clientwrapper.util.cookie

import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.result.accountant.api.util.StorageUtil
import neutrino.project.clientwrapper.OkHttpClientWrapper
import neutrino.project.clientwrapper.util.cookie.impl.ClientCookieHandler
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.io.FileNotFoundException
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

	override fun saveCookie() {
		val cookiesPath = client.cookiesFileName
		if (cookiesPath != null) {
			val saveFile = StorageUtil.Directory.COOKIE.fileInFolder("$cookiesPath.cookie")
			CookieFileStore.saveCookie(getCookies(), saveFile)
		}
	}

	override fun restoreCookies(fileName: String): List<HttpCookie> {
		var cookies = listOf<HttpCookie>()

		try {
			val saveFile = StorageUtil.Directory.COOKIE.fileInFolder("$fileName.cookie")
			cookies = CookieFileStore.restoreCookie(saveFile) ?: listOf()
		} catch (e: Exception) {
			when (e) {
				is FileNotFoundException -> e.printStackTrace()
				is JsonIOException -> e.printStackTrace()
				is JsonSyntaxException -> e.printStackTrace()
				else -> throw e
			}
		}

		return cookies
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