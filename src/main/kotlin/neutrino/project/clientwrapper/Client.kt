package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.global.GlobalClient
import neutrino.project.clientwrapper.processor.request.RequestProcessor
import neutrino.project.clientwrapper.processor.response.ResponseProcessor
import neutrino.project.clientwrapper.storage.DefaultStorageProvider
import neutrino.project.clientwrapper.storage.StorageProvider
import okhttp3.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


interface Client: TypedClient<Response?> {

	fun asGlobal(): GlobalClient {
		GlobalClient.setAsGlobal(this)
		return GlobalClient
	}

	companion object {
		const val defaultUserAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36"

		fun createDefault(baseUrl: String): Client {
			return create(baseUrl = baseUrl, isUnsafe = false)
		}

		fun create(baseUrl: String = "",
				   isUnsafe: Boolean = false,
				   interceptors: List<Interceptor> = listOf(),
				   requestProcessors: List<RequestProcessor> = listOf(),
				   responseProcessors: List<ResponseProcessor> = listOf(),
				   storageProvider: StorageProvider = DefaultStorageProvider(),
				   protocols: List<Protocol> = listOf(Protocol.HTTP_1_1),  // set HTTP_2 with JAVA 9
				   cookiesFilePath: String? = null,
				   cookieJar: CookieJar? = null,
				   executorService: ExecutorService = Executors.newWorkStealingPool(),
				   connectionPool: ConnectionPool? = null): Client {

			return OkHttpClientWrapper(
					baseUrl = baseUrl,
					isUnsafe = isUnsafe,
					interceptors = interceptors,
					requestProcessors = requestProcessors,
					responseProcessors = responseProcessors,
					cookiesFileName = cookiesFilePath,
					protocols = protocols,
					storageProvider = storageProvider,
					executors = executorService,
					cookieJar = cookieJar,
					connectionPool = connectionPool
			)
		}
	}
}