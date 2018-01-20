package neutrino.project.clientwrapper.request.executable

import okhttp3.Response
import java.util.*


interface ExecutableRequest {

	fun presentExecute(): Optional<Response>

	fun execute(): Response?

	fun executeAndGetBody(): Optional<String>
}