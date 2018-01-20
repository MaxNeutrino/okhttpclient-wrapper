package neutrino.project.clientwrapper.request.executable

import neutrino.project.clientwrapper.request.builder.AbstractRequestBuilder
import okhttp3.Call
import okhttp3.Response
import java.util.*


class FullExecutableRequest(requestBuilder: AbstractRequestBuilder) : ExecutableRequest, AsyncExecutableRequest {

	private val executable: ExecutableRequest by lazy { SimpleExecutableRequest(requestBuilder) }

	private val asyncExecutable: AsyncExecutableRequest by lazy { DefaultAsyncExecutableRequest(requestBuilder) }

	override fun presentExecute(): Optional<Response> = executable.presentExecute()

	override fun execute(): Response? = executable.execute()

	override fun executeAndGetBody(): Optional<String> = executable.executeAndGetBody()

	override fun withCallback(success: (call: Call?, response: Response) -> Unit,
							  failure: (call: Call?, e: Exception?) -> Unit) = asyncExecutable.withCallback(success,
			failure)

	override fun withResponse(success: (response: Response) -> Unit,
							  failure: (e: Exception?) -> Unit) = asyncExecutable.withResponse(success, failure)
}