package neutrino.project.clientwrapper.util.cookie

import javafx.scene.web.WebEngine
import neutrino.project.clientwrapper.Client
import java.net.CookieHandler


interface CookieManagerHandler {

	fun makeDefault(): CookieHandler

	fun setWebViewEngine(engine: WebEngine)

	fun loadCookieToClient(client: Client)
}