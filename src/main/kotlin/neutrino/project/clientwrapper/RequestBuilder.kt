package neutrino.project.clientwrapper

import okhttp3.Response
import java.util.*


interface RequestBuilder {

	fun url(url: String): RequestBuilder

	fun addHeader(name: String, value: String): RequestBuilder

	fun addHeaders(headers: Map<String, String>): RequestBuilder

	fun get(): RequestBuilder

	fun post(params: Map<String, String>): RequestBuilder

	fun execute(): Optional<Response>

	fun executeAndGetBody(): Optional<String>
}