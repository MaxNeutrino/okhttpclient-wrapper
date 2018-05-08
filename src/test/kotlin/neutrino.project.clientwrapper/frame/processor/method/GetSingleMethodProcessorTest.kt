package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.data.GetTestData
import neutrino.project.clientwrapper.data.GetTestDataMapper
import neutrino.project.clientwrapper.frame.Expected
import neutrino.project.clientwrapper.frame.GetMethod
import neutrino.project.clientwrapper.frame.MethodBuilder
import neutrino.project.clientwrapper.frame.EmptyResponseMapper
import neutrino.project.clientwrapper.frame.content.Countable
import neutrino.project.clientwrapper.params
import neutrino.project.clientwrapper.util.exception.ResponseMapperNotFoundException
import neutrino.project.clientwrapper.util.ext.listType
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class GetSingleMethodProcessorTest: AbstractMethodProcessorTest() {

	private val client = Client.createSimple("http://localhost:7000")

	@Test
	fun callReceiveTest() {
		val methodBuilder = MethodBuilder(client, Call::class)

		val method = GetMethod<Call>().apply {
			url = "/main/{param}"
			pathParams = params("param" to "1")
		}

		val expectedCall = methodBuilder.build(method).block()

		val okhttpClient = OkHttpClient.Builder().build()

		val request = Request.Builder()
				.url("http://localhost:7000/main/1")
				.build()

		val actualCall = okhttpClient.newCall(request)

		val expectedResponse = expectedCall.execute()!!
		val actualResponse = actualCall.execute()!!

		assertResponses(expectedResponse, actualResponse)
	}

	@Test
	fun responseReceiveTest() {
		val methodBuilder = MethodBuilder(client, Response::class)

		val method = GetMethod<Response>().apply {
			url = "/main/{param}"
			pathParams = params("param" to "1")
		}

		val okhttpClient = OkHttpClient.Builder().build()

		val request = Request.Builder()
				.url("http://localhost:7000/main/1")
				.build()

		val actualCall = okhttpClient.newCall(request)

		val expectedResponse = methodBuilder.build(method).block()
		val actualResponse = actualCall.execute()!!

		assertResponses(expectedResponse, actualResponse)
	}

	@Test
	fun blockMappedRequestTest() {
		sendAndAssertStringBody { it.block() }
	}

	@Test
	fun asyncMappedRequestTest() {
		sendAndAssertStringBody { it.async().get() }
	}

	@Test
	fun fakeAsyncMappedRequestTest() {
		sendAndAssertStringBody { it.fakeAsync().get() }
	}

	@Test
	fun countableResponseReceiveTest() {
		countableResponseReceive({ it.block() })
	}

	@Test
	fun countableAsyncResponseReceiveTest() {
		countableResponseReceive({ it.async().get() })
	}

	@Test
	fun countableFakeAsyncResponseReceiveTest() {
		countableResponseReceive({ it.fakeAsync().get() })
	}

	@Test
	fun mappingResponseTest() {
		val methodBuilder = MethodBuilder(client, GetTestData::class)

		val method = GetMethod<GetTestData>().apply {
			url = "/main/{param}"
			pathParams = params("param" to "1")

			responseMapper = GetTestDataMapper::class
		}

		val okhttpClient = OkHttpClient.Builder().build()

		val request = Request.Builder()
				.url("http://localhost:7000/main/1")
				.build()

		val actualCall = okhttpClient.newCall(request)

		val expected = methodBuilder.build(method).block()
		val actualResponse = actualCall.execute()!!

		val actual = GetTestDataMapper().map(actualResponse)

		Assertions.assertEquals(expected, actual)
	}

	@Test
	fun countableMappingResponseTest() {
		val methodBuilder = MethodBuilder(client, listType<GetTestData>())

		val method = GetMethod<List<GetTestData>>().apply {
			url = "/main/{param}"
			pathCountable = Countable("param", 0, 2, { _, response ->
				(response!!.code() == 404 || response.code() == 500)
						.also { response.close() } // close response for testing
			})

			responseMapper = GetTestDataMapper::class
		}

		val okhttpClient = OkHttpClient.Builder().build()

		val request1 = Request.Builder()
				.url("http://localhost:7000/main/0")
				.build()

		val request2 = Request.Builder()
				.url("http://localhost:7000/main/2")
				.build()

		val request3 = Request.Builder()
				.url("http://localhost:7000/main/4")
				.build()

		val request4 = Request.Builder()
				.url("http://localhost:7000/main/6")
				.build()

		val actualResponse1 = okhttpClient.newCall(request1).execute()!!
		val actualResponse2 = okhttpClient.newCall(request2).execute()!!
		val actualResponse3 = okhttpClient.newCall(request3).execute()!!
		val actualResponse4 = okhttpClient.newCall(request4).execute()!!

		val mapper = GetTestDataMapper()
		val actual1 = mapper.map(actualResponse1)
		val actual2 = mapper.map(actualResponse2)
		val actual3 = mapper.map(actualResponse3)
		val actual4 = mapper.map(actualResponse4)

		val expecteds = methodBuilder.build(method).block()

		Assertions.assertEquals(expecteds[0], actual1)
		Assertions.assertEquals(expecteds[1], actual2)
		Assertions.assertEquals(expecteds[2], actual3)
		Assertions.assertEquals(expecteds[3], actual4)
	}

	@Test
	fun mapperNotFoundTest() {
		val methodBuilder = MethodBuilder(client, GetTestData::class)

		val method = GetMethod<GetTestData>().apply {
			url = "/main/{param}"
			pathParams = params("param" to "1")
		}

		Assertions.assertThrows(ResponseMapperNotFoundException::class.java, { methodBuilder.build(method).block() })
	}

	private fun sendAndAssertStringBody(func: (Expected<String>) -> String) {
		val methodBuilder = MethodBuilder(client, String::class)

		val method = GetMethod<String>().apply {
			url = "/main/{param}"
			pathParams = params("param" to "1")
		}

		val okhttpClient = OkHttpClient.Builder().build()

		val request = Request.Builder()
				.url("http://localhost:7000/main/1")
				.build()

		val actualCall = okhttpClient.newCall(request)

		val expected = methodBuilder.build(method).let(func)
		val actualResponse = actualCall.execute()!!
		val actual = actualResponse.body()!!.string()

		Assertions.assertEquals(expected, actual)
	}

	private fun countableResponseReceive(
			expectedProcessor: (Expected<List<Response>>) -> List<Response>) {
		val methodBuilder = MethodBuilder(client, listType<Response>())

		val method = GetMethod<List<Response>>().apply {
			url = "/main/{param}"
			pathCountable = Countable("param", 0, 2, { _, response ->
				(response!!.code() == 404 || response.code() == 500)
						.also { response.close() } // close response for testing
			})

			responseMapper = EmptyResponseMapper::class
		}

		val okhttpClient = OkHttpClient.Builder().build()

		val request1 = Request.Builder()
				.url("http://localhost:7000/main/0")
				.build()

		val request2 = Request.Builder()
				.url("http://localhost:7000/main/2")
				.build()

		val request3 = Request.Builder()
				.url("http://localhost:7000/main/4")
				.build()

		val request4 = Request.Builder()
				.url("http://localhost:7000/main/6")
				.build()

		val actualResponse1 = okhttpClient.newCall(request1).execute()!!
		val actualResponse2 = okhttpClient.newCall(request2).execute()!!
		val actualResponse3 = okhttpClient.newCall(request3).execute()!!
		val actualResponse4 = okhttpClient.newCall(request4).execute()!!

		val expectedResponses = methodBuilder.build(method).let(expectedProcessor)

		assertResponses(expectedResponses[0], actualResponse1)
		assertResponses(expectedResponses[1], actualResponse2)
		assertResponses(expectedResponses[2], actualResponse3)
		assertResponses(expectedResponses[3], actualResponse4)
	}
}