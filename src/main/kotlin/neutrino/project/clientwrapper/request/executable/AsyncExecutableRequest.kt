package neutrino.project.clientwrapper.request.executable

import okhttp3.Call
import okhttp3.Response


interface AsyncExecutableRequest {

	fun withCallback(success: (call: Call?, response: Response) -> Unit,
					 failure: (call: Call?, e: Exception?) -> Unit = { _, e -> if(e != null) throw e })

	fun withResponse(success: (response: Response) -> Unit,
					 failure: (e: Exception?) -> Unit = { e -> if(e != null) throw e })
}