package neutrino.project.clientwrapper.util.cookie

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import neutrino.project.clientwrapper.Client
import java.io.*
import java.net.HttpCookie
import java.net.URI


object CookieFileStore {

	@Throws(IOException::class)
	fun saveCookie(cookies: List<HttpCookie>?, saveFile: File) {
		FileWriter(saveFile).use {
			val to = cookies?.map { CookieTo.of(it) } ?: emptyList()
			val data = JSON.stringify(to)
			it.write(data)
			it.flush()
		}
	}

	@Throws(IOException::class)
	fun saveCookie(cookies: List<HttpCookie>?, saveFile: String) {
		saveCookie(cookies, File(saveFile))
	}

	@Throws(IOException::class)
	fun saveCookie(client: Client, saveFile: File) {
		val cookies = client.getClientCookieHandler()?.getCookieStore()?.cookies
		saveCookie(cookies, saveFile)
	}

	@Throws(IOException::class)
	fun saveCookie(client: Client, saveFile: String) {
		val cookies = client.getClientCookieHandler()?.getCookieStore()?.cookies
		saveCookie(cookies, saveFile)
	}

	@Throws(IOException::class)
	fun restoreCookie(saveFile: File): List<HttpCookie>? {
		return BufferedReader(FileReader(saveFile)).use {
			val data = it.readText()
			val to = JSON.parse<List<CookieTo>>(data)
			return@use to.map { it.toHttpCookie() }
		}
	}

	@Throws(IOException::class)
	fun restoreCookie(saveFile: String): List<HttpCookie>? {
		return restoreCookie(File(saveFile))
	}

	@Throws(IOException::class)
	fun restoreCookie(client: Client, saveFile: File) {
		val store = client.getClientCookieHandler()?.getCookieStore()
		restoreCookie(saveFile)?.forEach { cookie ->
			store?.add(URI(client.getBaseUrl()), cookie)
		}
	}

	fun restoreCookie(client: Client, saveFile: String) {
		restoreCookie(client, File(saveFile))
	}

	@Serializable
	data class CookieTo(val name: String, val value: String, val domain: String) {

		fun toHttpCookie(): HttpCookie {
			val cookie = HttpCookie(name, value)
			cookie.domain = domain
			return cookie
		}

		companion object {
			fun of(cookie: HttpCookie): CookieTo {
				return CookieTo(
						cookie.name,
						cookie.value,
						cookie.domain
				)
			}
		}
	}
}