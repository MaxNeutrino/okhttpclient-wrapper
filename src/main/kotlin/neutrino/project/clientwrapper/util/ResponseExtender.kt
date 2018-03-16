package neutrino.project.clientwrapper.util

import okhttp3.Call
import okhttp3.Response
import okhttp3.ResponseBody

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