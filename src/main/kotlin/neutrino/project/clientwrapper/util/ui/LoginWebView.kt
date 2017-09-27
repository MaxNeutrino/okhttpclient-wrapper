package neutrino.project.clientwrapper.util.ui

import javafx.concurrent.Worker
import javafx.scene.Scene
import javafx.scene.web.WebView
import javafx.stage.Stage
import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.util.cookie.CookieManagerHandler
import neutrino.project.clientwrapper.util.cookie.impl.NetCookieManagerHandler

/**
 * Init Login view by default with
 */
class LoginWebView(private val loginUrl: String,
				   private val successUrl: String? = null,
				   private val stage: Stage = Stage(),
				   private val client: Client,
				   private val cookieManagerHandler: CookieManagerHandler = NetCookieManagerHandler(),
				   private val successFunc: ((page: String) -> Boolean)? = null) {

	private val webView = WebView()

	init {
		cookieManagerHandler.setWebViewEngine(webView.engine)
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
		cookieManagerHandler.loadCookieToClient(client)
		stage.close()
	}
}