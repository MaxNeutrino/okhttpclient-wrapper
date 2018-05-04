package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.MockWebServer
import okhttp3.Response
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll


abstract class AbstractMethodProcessorTest {

	companion object {
		private val server = MockWebServer()

		@BeforeAll
		@JvmStatic
		fun beforeAll() {
			server.start()
		}

		@AfterAll
		@JvmStatic
		fun afterAll() {
			server.stop()
		}
	}

	protected fun assertResponses(expectedResponse: Response, actualResponse: Response) {
		Assertions.assertEquals(expectedResponse.code(), actualResponse.code())
		Assertions.assertEquals(expectedResponse.message(), actualResponse.message())
		Assertions.assertEquals(expectedResponse.isSuccessful, actualResponse.isSuccessful)
		Assertions.assertEquals(expectedResponse.protocol().name, actualResponse.protocol().name)
		Assertions.assertEquals(expectedResponse.body()?.string(), actualResponse.body()?.string())
	}
}