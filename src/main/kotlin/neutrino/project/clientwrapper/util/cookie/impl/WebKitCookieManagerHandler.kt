package neutrino.project.clientwrapper.util.cookie.impl

import com.sun.webkit.network.CookieManager
import javafx.scene.web.WebEngine
import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.util.cookie.CookieManagerHandler
import java.net.CookieHandler

/**
 * For some websites
 * If your credentials right but cookie wasn't load than use it
 */
class WebKitCookieManagerHandler : CookieManagerHandler {

	private val cookieManager = CookieManager()
	private var webEngine: WebEngine? = null

	init {
		CookieHandler.setDefault(cookieManager)
	}

	override fun setWebViewEngine(engine: WebEngine) {
		webEngine = engine
	}

	override fun loadCookieToClient(client: Client) {
		val cookieLine = webEngine?.executeScript("document.cookie") as String
		val cookies = cookieLine.split("; ")

		cookies.forEach {
			val cookie = it.split("=")
			client.getClientCookieHandler().addCookie(cookie[0], cookie[1])
		}
	}
}