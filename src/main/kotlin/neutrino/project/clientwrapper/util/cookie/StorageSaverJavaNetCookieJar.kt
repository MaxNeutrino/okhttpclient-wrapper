package neutrino.project.clientwrapper.util.cookie

import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.internal.platform.Platform
import java.io.*
import java.net.CookieHandler


class StorageSaverJavaNetCookieJar(private var cookieHandler: CookieHandler,
								   private val saveFile: File) : JavaNetCookieJar(cookieHandler) {

	private var isRestored = false

	private var lastCookieHeaders = mutableListOf<String>()

	override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
		super.saveFromResponse(url, cookies)

		try {
			val headers = emptyMap<String, List<String>>()
			val allHeaders = cookieHandler.get(url.uri(), headers)
			val cookieHeaders = getCookiesHeaders(allHeaders)

			if (cookieHeaders != lastCookieHeaders) {
				lastCookieHeaders = cookieHeaders

				val fileOutputStream = FileOutputStream(saveFile)
				val objectOutputStream = ObjectOutputStream(fileOutputStream)

				objectOutputStream.writeObject(cookieHeaders)

				objectOutputStream.close()
				fileOutputStream.close()
			}

		} catch (e: IOException) {
			Platform.get().log(Platform.WARN, "Loading cookies failed for " + url.resolve("/...")!!, e)
		}
	}

	override fun loadForRequest(url: HttpUrl): List<Cookie> {
		if (!isRestored) {
			try {
				val fileInputStream = FileInputStream(saveFile)
				val objectInputStream = ObjectInputStream(fileInputStream)

				val cookieHeaders = objectInputStream.readObject() as MutableList<String>

				objectInputStream.close()
				fileInputStream.close()

				val cookies = cookieHeaders
						.flatMap { decodeHeaderAsJavaNetCookies(url, it) }
						.toMutableList()

				if (cookies != null) {
					saveFromResponse(url, cookies)
				}

			} catch (e: IOException) {
				Platform.get().log(Platform.WARN, "Loading cookies failed for " + url.resolve("/...")!!, e)
			}
		}

		return super.loadForRequest(url)
	}
}