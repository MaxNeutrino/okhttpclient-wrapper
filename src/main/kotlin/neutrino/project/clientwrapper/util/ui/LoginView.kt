package neutrino.project.clientwrapper.util.ui

import javafx.concurrent.Worker
import javafx.scene.Scene
import javafx.scene.web.WebView
import javafx.stage.Stage
import neutrino.project.clientwrapper.Client
import java.net.CookieHandler

/**
 * Init Login view by default with
 */
class LoginView(private val loginUrl: String,
				private val successUrl: String? = null,
				private val stage: Stage = Stage(),
				private val client: Client,
				private val successFunc: ((page: String) -> Boolean)? = null) {

	private val webView = WebView()
	private val cc: com.sun.webkit.network.CookieManager = com.sun.webkit.network.CookieManager()

	init {
		CookieHandler.setDefault(cc)
		webView.engine.load(loginUrl)
		setSuccessListener()
	}

	fun showAndAuth() {
		stage.title = "login"
		stage.scene = Scene(webView)
		stage.showAndWait()
	}

	private fun setSuccessListener() {
		webView.engine.loadWorker.stateProperty().addListener({ _, _, newValue ->
			if (newValue == Worker.State.SUCCEEDED) {

				if (successUrl == null && successFunc != null) {
					val page = webView.engine.executeScript("document.body.innerHTML") as String
					val isSuccess = successFunc.invoke(page)
					if (isSuccess) {
						doWithSuccess()
					}
				} else {
					val location = webView.engine.location
					if (location == successUrl) {
						doWithSuccess()
					}
				}
			}
		})
	}

	private fun doWithSuccess() {
		val cookieLine = webView.engine.executeScript("document.cookie") as String
		val cookies = cookieLine.split("; ")

		/*val convertedCookies = cookies.map {
        val cookie = it.split("=")
        return@map Cookie.Builder()
                .name(cookie[0])
                .value(cookie[1])
                .domain(client.baseUrl)
                .build()
    }.toMutableList()

    client.cookieHandler.addCookieToClient(convertedCookies)*/
		cookies.forEach {
			val cookie = it.split("=")
			client.cookieHandler.addCookie(cookie[0], cookie[1])
		}
		stage.close()
	}
}