package neutrino.project.clientwrapper.util.cookie.impl

import okhttp3.Cookie
import java.net.CookieStore
import java.net.HttpCookie


interface ClientCookieHandler {

	fun addCookie(cookie: HttpCookie)

	fun addCookie(cookies: List<HttpCookie>)

	fun addCookie(name: String, value: String)

	fun addCookie(cookie: Cookie)

	fun addCookieToClient(cookies: MutableList<Cookie>)

	fun getCookies(): List<HttpCookie>

	fun getCookieStore(): CookieStore?
}