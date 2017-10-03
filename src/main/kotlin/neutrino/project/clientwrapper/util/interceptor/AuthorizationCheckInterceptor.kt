package neutrino.project.clientwrapper.util.interceptor

import neutrino.project.clientwrapper.util.exception.AuthException
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody


class AuthorizationCheckInterceptor(private val pageContainsFunction: (body: String?) -> Boolean) : Interceptor {

	override fun intercept(chain: Interceptor.Chain?): Response {
		if (chain != null) {
			val request = chain.request()
			val response = chain.proceed(request)
			val body = response.body()?.string()

			val isAuth = pageContainsFunction.invoke(body)
			if (!isAuth)
				throw AuthException("page haven't expected info")

			return response.newBuilder().body(ResponseBody.create(response.body()!!.contentType(), body)).build()
		} else {
			throw AuthException("okhttp chain is null")
		}
	}
}