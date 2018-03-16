package neutrino.project.clientwrapper.util.cookie

import kotlinx.serialization.Serializable
import okhttp3.Cookie
import java.net.HttpCookie


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

		fun of(cookie: Cookie): CookieTo {
			return CookieTo(
					cookie.name(),
					cookie.value(),
					cookie.domain()
			)
		}
	}
}