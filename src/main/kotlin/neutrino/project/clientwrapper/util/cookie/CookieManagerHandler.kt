package neutrino.project.clientwrapper.util.cookie

import javafx.scene.web.WebEngine
import neutrino.project.clientwrapper.Client


interface CookieManagerHandler {

	fun setWebViewEngine(engine: WebEngine)

	fun loadCookieToClient(client: Client)
}