package neutrino.project.clientwrapper.util.ui

import javafx.beans.value.ChangeListener
import javafx.concurrent.Worker
import javafx.scene.Scene
import javafx.scene.web.WebView
import javafx.stage.Stage
import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.util.cookie.CookieManagerHandler
import neutrino.project.clientwrapper.util.cookie.impl.NetCookieManagerHandler


/**
 * Init Login fragment by default with
 */
class LoginWebView(private val loginUrl: String,
				   private val successUrl: String? = null,
				   private val stage: Stage = Stage(),
				   private val client: Client?,
				   val cookieManagerHandler: CookieManagerHandler = NetCookieManagerHandler(),
				   private val successFunc: ((page: String) -> Boolean)? = null,
				   val adminPanelName: String = "") {

	private val webView = WebView()

	init {
		cookieManagerHandler.setWebViewEngine(webView.engine)
		webView.engine.load(loginUrl)
		setLoadedContentListener()
		setSuccessListener()
	}

	fun showAndAuth() {
		stage.title = "login $adminPanelName"
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
		if (client != null) {
			cookieManagerHandler.loadCookieToClient(client)
		}
		stage.close()
	}

	private fun setLoadedContentListener() {
		var isLoaded = false
		webView.engine.loadWorker.stateProperty().addListener(
				ChangeListener<Worker.State> { observable, oldValue, newValue ->
					if (newValue != Worker.State.SUCCEEDED) {
						return@ChangeListener
					}

					if (!isLoaded) {
						cookieManagerHandler.makeDefault()
						isLoaded = true
					}
				})
	}
}