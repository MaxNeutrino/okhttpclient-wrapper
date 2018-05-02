package neutrino.project.clientwrapper.util.cookie

import neutrino.project.clientwrapper.Client
import java.io.*
import java.net.HttpCookie
import java.net.URI


object CookieFileStore {

	@Throws(IOException::class)
	fun saveCookie(cookies: List<HttpCookie>?, saveFile: File) {
		ObjectOutputStream(FileOutputStream(saveFile)).use {
			val to = cookies?.map { CookieTo.of(it) } ?: emptyList()
			it.writeObject(to)
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
	@Suppress("UNCHECKED_CAST")
	fun restoreCookie(saveFile: File): List<HttpCookie>? {
		return if (saveFile.exists())
			ObjectInputStream(FileInputStream(saveFile)).use {
				val to = it.readObject() as List<CookieTo>
				return@use to.map { it.toHttpCookie() }
			}
		else
			emptyList()
	}

	@Throws(IOException::class)
	fun restoreCookie(saveFile: String): List<HttpCookie>? {
		val file = File(saveFile)
		return if (file.exists())
			restoreCookie(file)
		else
			emptyList()
	}

	@Throws(IOException::class)
	fun restoreCookie(client: Client, saveFile: File) {
		if (saveFile.exists()) {
			val store = client.getClientCookieHandler()?.getCookieStore()
			restoreCookie(saveFile)?.forEach { cookie ->
				store?.add(URI(client.getBaseUrl()), cookie)
			}
		}
	}

	fun restoreCookie(client: Client, saveFile: String) {
		val file = File(saveFile)
		if (file.exists())
			restoreCookie(client, file)
	}
}