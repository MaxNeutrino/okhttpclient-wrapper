package neutrino.project.clientwrapper.util

import okhttp3.Cache
import java.io.File
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object BuildUtil {

	fun getCache(child: String): Cache {
		val cacheDir = File(System.getProperty("java.io.tmpdir"), child)
		return Cache(cacheDir, 1024)
	}

	fun createUnsafeSSL(trustManager: TrustManager): SSLSocketFactory {
		val trustAllCerts = arrayOf(trustManager)
		val sc = SSLContext.getInstance("SSL")
		sc.init(null, trustAllCerts, java.security.SecureRandom())

		return sc.socketFactory
	}

	private fun createTrustManager() = object : X509TrustManager {

		override fun getAcceptedIssuers(): Array<X509Certificate> {
			return emptyArray()
		}

		override fun checkClientTrusted(certs: Array<java.security.cert.X509Certificate>, authType: String) {
			//No need to implement.
		}

		override fun checkServerTrusted(certs: Array<java.security.cert.X509Certificate>, authType: String) {
			//No need to implement.
		}
	}
}