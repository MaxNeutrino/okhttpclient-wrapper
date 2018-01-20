package neutrino.project.clientwrapper.processor.request

import neutrino.project.clientwrapper.Client
import okhttp3.Request


class HeaderTokenRequestProcessor : TokenRequestProcessor {

	private var token = ""

	override fun setToken(token: String) {
		this.token = token
	}

	override fun getToken(): String = token

	override fun removeToken() {
		token = ""
	}

	override fun process(client: Client, requestBuilder: Request.Builder): Request.Builder {
		if (token.isNotEmpty()) {
			requestBuilder.addHeader("Authorization", "Bearer $token")
		}

		return requestBuilder
	}
}