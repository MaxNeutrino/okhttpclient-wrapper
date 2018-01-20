package neutrino.project.clientwrapper.util.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.internal.Util.delimiterOffset
import okhttp3.internal.Util.trimSubstring
import okhttp3.internal.http.HttpDate
import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.Platform.WARN
import java.io.IOException
import java.net.CookieHandler
import java.net.HttpCookie
import java.util.*

/**
 * OkHttp implementation from urlconnection
 * copied for resolving package names issues in Java 9 Jigsaw
 */
class JavaNetCookieJar(private var cookieHandler: CookieHandler) : CookieJar {

	override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
		val cookieStrings = cookies.map { toString(it, true) }
		val multimap = Collections.singletonMap<String, List<String>>("Set-Cookie", cookieStrings)
		try {
			cookieHandler.put(url.uri(), multimap)
		} catch (e: IOException) {
			Platform.get().log(WARN, "Saving cookies failed for " + url.resolve("/...")!!, e)
		}
	}

	override fun loadForRequest(url: HttpUrl): List<Cookie> {
		// The RI passes all headers. We don't have 'em, so we don't pass 'em!
		val headers = emptyMap<String, List<String>>()
		val cookieHeaders: Map<String, List<String>>
		try {
			cookieHeaders = cookieHandler.get(url.uri(), headers)
		} catch (e: IOException) {
			Platform.get().log(WARN, "Loading cookies failed for " + url.resolve("/...")!!, e)
			return emptyList()
		}

		var cookies: MutableList<Cookie>? = null
		for ((key, value) in cookieHeaders) {
			if (("Cookie".equals(key, ignoreCase = true) || "Cookie2".equals(key,
							ignoreCase = true)) && !value.isEmpty()) {
				for (header in value) {
					if (cookies == null) cookies = ArrayList()
					cookies.addAll(decodeHeaderAsJavaNetCookies(url, header))
				}
			}
		}

		return if (cookies != null)
			Collections.unmodifiableList(cookies)
		else
			emptyList()
	}

	/**
	 * Convert a request header to OkHttp's cookies via [HttpCookie]. That extra step handles
	 * multiple cookies in a single request header, which [Cookie.parse] doesn't support.
	 */
	private fun decodeHeaderAsJavaNetCookies(url: HttpUrl, header: String): List<Cookie> {
		val result = ArrayList<Cookie>()
		var pos = 0
		val limit = header.length
		var pairEnd: Int
		while (pos < limit) {
			pairEnd = delimiterOffset(header, pos, limit, ";,")
			val equalsSign = delimiterOffset(header, pos, pairEnd, '=')
			val name = trimSubstring(header, pos, equalsSign)
			if (name.startsWith("$")) {
				pos = pairEnd + 1
				continue
			}

			// We have either name=value or just a name.
			var value = if (equalsSign < pairEnd)
				trimSubstring(header, equalsSign + 1, pairEnd)
			else
				""

			// If the value is "quoted", drop the quotes.
			if (value.startsWith("\"") && value.endsWith("\"")) {
				value = value.substring(1, value.length - 1)
			}

			result.add(Cookie.Builder()
					.name(name)
					.value(value)
					.domain(url.host())
					.build())
			pos = pairEnd + 1
		}
		return result
	}

	private fun toString(cookie: Cookie, forObsoleteRfc2965: Boolean): String {
		val result = StringBuilder()
		result.append(cookie.name())
		result.append('=')
		result.append(cookie.value())

		if (cookie.persistent()) {
			if (cookie.expiresAt() == java.lang.Long.MIN_VALUE) {
				result.append("; max-age=0")
			} else {
				result.append("; expires=").append(HttpDate.format(Date(cookie.expiresAt())))
			}
		}

		if (!cookie.hostOnly()) {
			result.append("; domain=")
			if (forObsoleteRfc2965) {
				result.append(".")
			}
			result.append(cookie.domain())
		}

		result.append("; path=").append(cookie.path())

		if (cookie.secure()) {
			result.append("; secure")
		}

		if (cookie.httpOnly()) {
			result.append("; httponly")
		}

		return result.toString()
	}
}