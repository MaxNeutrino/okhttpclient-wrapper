package neutrino.project.clientwrapper.type

import neutrino.project.clientwrapper.request.executable.AsyncExecutableRequest
import neutrino.project.clientwrapper.request.executable.AsyncExecutable
import okhttp3.Call
import okhttp3.Response


open class CustomAsyncExecutableRequest<T>(protected val defaultAsyncExecutable: AsyncExecutableRequest,
									  protected val wrapFunc: (Response?) -> T) : AsyncExecutable<T> {

	override fun withCallback(success: (call: Call?, response: T) -> Unit,
							  failure: (call: Call?, e: Exception?) -> Unit) {
		defaultAsyncExecutable.withCallback(
				success = { c, r ->
					val wrappedResponse = wrapFunc.invoke(r)
					success.invoke(c, wrappedResponse)
				},
				failure = { c, e -> failure.invoke(c, e) }
		)
	}

	override fun withResponse(success: (response: T) -> Unit, failure: (e: Exception?) -> Unit) {
		defaultAsyncExecutable.withResponse(
				success = {
					val wrappedResponse = wrapFunc.invoke(it)
					success.invoke(wrappedResponse)
				},
				failure = { failure.invoke(it) }
		)
	}
}