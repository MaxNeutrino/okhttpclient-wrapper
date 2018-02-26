package neutrino.project.clientwrapper.request.executable

import okhttp3.Call
import okhttp3.Response


interface AsyncExecutable<T> {

	fun withCallback(success: (call: Call?, response: T) -> Unit,
					 failure: (call: Call?, e: Exception?) -> Unit = { _, e -> if(e != null) throw e })

	fun withResponse(success: (response: T) -> Unit,
					 failure: (e: Exception?) -> Unit = { e -> if(e != null) throw e })
}