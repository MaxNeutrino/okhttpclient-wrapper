package neutrino.project.clientwrapper.util.ext

import neutrino.project.clientwrapper.frame.ResponseConsumer
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.util.concurrent.CompletableFuture

fun Response.stringBody(): String? = this.body()?.string()

fun <T> Response.closeAfter(block: (response: Response) -> T): T {
	return block.invoke(this)
			.also { close() }
}

fun Response.applyBody(block: (body: ResponseBody?) -> Unit) {
	block.invoke(this.body())
}

fun Response.applyStringBody(block: (body: String?) -> Unit) {
	block.invoke(this.stringBody())
}

fun <T> Response.letBody(block: (body: ResponseBody?) -> T): T {
	return block.invoke(this.body())
}

fun <T> Response.letStringBody(block: (body: String?) -> T): T {
	return block.invoke(this.stringBody())
}

fun Call.blockStringBody(): String? = this.execute().stringBody()

fun <T> Call.letStringBody(block: (body: String?) -> T): T = this.execute().letStringBody(block)

fun <T> Call.letBody(block: (body: ResponseBody?) -> T): T = this.execute().letBody(block)

fun <T> Call.blockAndClose(block: (response: Response) -> T): T = this.execute().closeAfter(block)

fun Call.applyBody(block: (body: ResponseBody?) -> Unit) = this.execute().applyBody(block)

fun Call.applyStringBody(block: (body: String?) -> Unit) = this.execute().applyStringBody(block)

fun Call.executeAsync(): CompletableFuture<Response> {
	val future = CompletableFuture<Response>()
	this.enqueue(object : Callback {
		override fun onResponse(call: Call, response: Response) {
			future.complete(response)
		}

		override fun onFailure(call: Call, e: IOException) {
			future.completeExceptionally(e)
		}
	})

	return future
}

fun <T> Collection<ResponseConsumer<T>?>.collect(): ResponseConsumer<T> {
	val responses: MutableList<Response> = mutableListOf()
	this.forEach {
		if (it?.response != null)
			responses.add(it.response)

		if (it?.responses != null)
			responses.addAll(it.responses!!)
	}

	val responseModels: MutableList<T> = mutableListOf()
	this.forEach {
		if (it?.responseModel != null)
			responseModels.add(it.responseModel)

		if (it?.responseModels != null)
			responseModels.addAll(it.responseModels!!)
	}

	return ResponseConsumer(
			responses = if (responses.isEmpty()) null else responses,
			responseModels = if (responseModels.isEmpty()) null else responseModels
	)
}