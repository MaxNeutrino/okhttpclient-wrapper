package neutrino.project.clientwrapper.cookie

import com.google.gson.GsonBuilder
import java.net.HttpCookie
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import neutrino.project.clientwrapper.Client
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.net.URI


object CookieFileStore {

	private val jsonType = object : TypeToken<List<HttpCookie>>() {}.type

	fun saveCookie(cookies: List<HttpCookie>?, saveFile: File) {
		FileWriter(saveFile).use { writer ->
			val gson = GsonBuilder().create()
			gson.toJson(cookies, writer)
		}
	}

	fun saveCookie(cookies: List<HttpCookie>?, saveFile: String) {
		saveCookie(cookies, File(saveFile))
	}

	fun saveCookie(client: Client, saveFile: File) {
		val cookies = client.cookieManager?.cookieStore?.cookies
		saveCookie(cookies, saveFile)
	}

	fun saveCookie(client: Client, saveFile: String) {
		val cookies = client.cookieManager?.cookieStore?.cookies
		saveCookie(cookies, saveFile)
	}

	fun restoreCookie(saveFile: File): List<HttpCookie>? {
		val cookies: MutableList<HttpCookie> = mutableListOf()
		JsonReader(FileReader(saveFile)).use { reader ->
			val gson = GsonBuilder().create()
			cookies.addAll(
					gson.fromJson(reader, jsonType))
		}
		return cookies
	}

	fun restoreCookie(saveFile: String): List<HttpCookie>? {
		return restoreCookie(File(saveFile))
	}

	fun restoreCookie(client: Client, saveFile: File) {
		val store = client.cookieManager?.cookieStore
		restoreCookie(saveFile)?.forEach { cookie ->
			store?.add(URI(client.baseUrl), cookie)
		}
	}

	fun restoreCookie(client: Client, saveFile: String) {
		restoreCookie(client, File(saveFile))
	}
}