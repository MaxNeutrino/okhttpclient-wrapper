package neutrino.project.clientwrapper.util.cookie

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
			val fos = FileOutputStream(saveFile)
			ObjectOutputStream(fos).use {
				val to = cookies.map { CookieTo.of(it) }
				it.writeObject(to)
				it.flush()
			}
			fos.close()

		} catch (e: IOException) {
			Platform.get().log(Platform.WARN, "Loading cookies failed for " + url.resolve("/...")!!, e)
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun loadForRequest(url: HttpUrl): List<Cookie> {
		if (!isRestored) {
			try {
				FileInputStream(saveFile).use {
					ObjectInputStream(it).use {
						val to = it.readObject() as List<CookieTo>

						to.map { it.toHttpCookie() }
					}
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