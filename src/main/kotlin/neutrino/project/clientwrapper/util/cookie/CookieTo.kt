package neutrino.project.clientwrapper.util.cookie

import okhttp3.Cookie
import java.io.Serializable
import java.net.HttpCookie

data class CookieTo(val name: String, val value: String, val domain: String): Serializable {

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

		fun of(cookie: Cookie): CookieTo {
			return CookieTo(
					cookie.name(),
					cookie.value(),
					cookie.domain()
			)
		}
	}
}