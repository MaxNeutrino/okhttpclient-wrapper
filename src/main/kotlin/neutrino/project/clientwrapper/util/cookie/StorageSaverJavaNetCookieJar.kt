package neutrino.project.clientwrapper.util.cookie

import kotlinx.serialization.json.JSON
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.internal.platform.Platform
import java.io.*
import java.net.CookieHandler


class StorageSaverJavaNetCookieJar(cookieHandler: CookieHandler,
								   private val saveFile: File) : JavaNetCookieJar(cookieHandler) {

	private var isRestored = false

	override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
		super.saveFromResponse(url, cookies)

		try {
			FileWriter(saveFile).use {
				val to = cookies.map { CookieTo.of(it) }
				val data = JSON.stringify(to)
				it.write(data)
				it.flush()
			}

		} catch (e: IOException) {
			Platform.get().log(Platform.WARN, "Loading cookies failed for " + url.resolve("/...")!!, e)
		}
	}

	override fun loadForRequest(url: HttpUrl): List<Cookie> {
		if (!isRestored) {
			try {
				BufferedReader(FileReader(saveFile)).use {
					val data = it.readText()
					val to = JSON.parse<List<CookieTo>>(data)
					return@use to.map { it.toHttpCookie() }
				}.map {
					Cookie.Builder()
							.name(it.name)
							.value(it.value)
							.domain(it.domain)
							.build()
				}.also {
					saveFromResponse(url, it)
				}

			} catch (e: IOException) {
				Platform.get().log(Platform.WARN, "Loading cookies failed for " + url.resolve("/...")!!, e)
			} finally {
				isRestored = true
			}
		}

		return super.loadForRequest(url)
	}
}