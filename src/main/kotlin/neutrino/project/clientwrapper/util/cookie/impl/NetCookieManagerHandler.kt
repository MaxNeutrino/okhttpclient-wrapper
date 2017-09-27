package neutrino.project.clientwrapper.util.cookie.impl

import javafx.scene.web.WebEngine
import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.util.cookie.CookieManagerHandler
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy

/**
 * It is default implementation for LoginWebView
 * For some websites need use other implementation
 * If your credentials right but cookie wasn't load than use @see WebKitCookieManagerHandler
 */
class NetCookieManagerHandler : CookieManagerHandler {

	private val cookieManager = CookieManager()

	init {
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
		CookieHandler.setDefault(cookieManager)
	}

	override fun setWebViewEngine(engine: WebEngine) {

	}

	override fun loadCookieToClient(client: Client) {
		val cookies = cookieManager.cookieStore.cookies
		client.getClientCookieHandler().addCookie(cookies)
	}
}