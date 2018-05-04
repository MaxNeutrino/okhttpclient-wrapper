package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.data.GetTestData
import neutrino.project.clientwrapper.data.GetTestDataMapper
import neutrino.project.clientwrapper.frame.*
import neutrino.project.clientwrapper.util.exception.RequestMethodException
import neutrino.project.clientwrapper.util.exception.ResponseMapperNotFoundException
import neutrino.project.clientwrapper.util.ext.listType
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class GetIterableMethodProcessorTest : AbstractMethodProcessorTest() {

	private val client = Client.createSimple("http://localhost:7000")

	@Test
	fun callReceiveTest() {
		val methodBuilder = MethodBuilder(client, Call::class)

		val method = GetMethod<Call>().apply {
			url = "/main/{param}"
			pathModel = listOf(GetTestData(0), GetTestData(1), GetTestData(2))
		}

		Assertions.assertThrows(RequestMethodException::class.java, {
			val call = methodBuilder.build(method).block()
			val response = call.execute()
			println(response.body()?.string())
		})
	}

	@Test
	fun responseReceiveTest() {
		val methodBuilder = MethodBuilder(client, listType<Response>())

		val method = GetMethod<List<Response>>().apply {
			url = "/main/{param}"
			pathModel = listOf(GetTestData(0), GetTestData(1), GetTestData(2))

			responseMapper = ResponseMapperImpl::class
		}

		val expecteds = methodBuilder.build(method).block()

		val okHttpClient = OkHttpClient()

		val request1 = Request.Builder().url("http://localhost:7000/main/0").build()
		val request2 = Request.Builder().url("http://localhost:7000/main/1").build()
		val request3 = Request.Builder().url("http://localhost:7000/main/2").build()

		val response1 = okHttpClient.newCall(request1).execute()
		val response2 = okHttpClient.newCall(request2).execute()
		val response3 = okHttpClient.newCall(request3).execute()

		assertResponses(expecteds[0], response1)
		assertResponses(expecteds[1], response2)
		assertResponses(expecteds[2], response3)
	}

	@Test
	fun stringReceiveTest() {
		val s = listOf("", "")
		val methodBuilder = MethodBuilder(client, s::class)

		val method = GetMethod<List<String>>().apply {
			url = "/main/{param}"
			pathModel = listOf(GetTestData(0), GetTestData(1), GetTestData(2))

			responseMapper = StringResponseMapper::class
		}

		val expecteds = methodBuilder.build(method).block()

		val okHttpClient = OkHttpClient()

		val request1 = Request.Builder().url("http://localhost:7000/main/0").build()
		val request2 = Request.Builder().url("http://localhost:7000/main/1").build()
		val request3 = Request.Builder().url("http://localhost:7000/main/2").build()

		val response1 = okHttpClient.newCall(request1).execute().body()!!.string()
		val response2 = okHttpClient.newCall(request2).execute().body()!!.string()
		val response3 = okHttpClient.newCall(request3).execute().body()!!.string()

		Assertions.assertEquals(expecteds, listOf(response1, response2, response3))
	}

	@Test
	fun blockMappedReceiveTest() {
		mappedRequestAssertion { it.block() }
	}

	@Test
	fun asyncMappedReceiveTest() {
		mappedRequestAssertion { it.async().get() }
	}

	@Test
	fun fakeAsyncMappedReceiveTest() {
		mappedRequestAssertion { it.fakeAsync().get() }
	}

	@Test
	fun blockParallelMappedReceiveTest() {
		parallelMappedRequestAssertion { it.block() }
	}

	@Test
	fun asyncParallelMappedReceiveTest() {
		parallelMappedRequestAssertion { it.async().get() }
	}

	@Test
	fun fakeParallelAsyncMappedReceiveTest() {
		parallelMappedRequestAssertion { it.fakeAsync().get() }
	}

	@Test
	fun responseMapperNotFoundTest() {
		val methodBuilder = MethodBuilder(client, listType<GetTestData>())

		val method = GetMethod<List<GetTestData>>().apply {
			url = "/main/{param}"
			pathModel = listOf(GetTestData(0), GetTestData(1), GetTestData(2))
		}

		Assertions.assertThrows(ResponseMapperNotFoundException::class.java, {
			val expecteds = methodBuilder.build(method).block()
			println(expecteds)
		})

	}

	private fun mappedRequestAssertion(processor: (Expected<List<GetTestData>>) -> List<GetTestData>) {
		requestAssertion({
			GetMethod<List<GetTestData>>().apply {
				url = "/main/{param}"
				pathModel = listOf(GetTestData(0), GetTestData(1), GetTestData(2))

				responseMapper = GetTestDataMapper::class
			}
		}, processor)
	}

	private fun parallelMappedRequestAssertion(processor: (Expected<List<GetTestData>>) -> List<GetTestData>) {
		requestAssertion({
			GetMethod<List<GetTestData>>().apply {
				url = "/main/{param}"
				parallel = true
				pathModel = listOf(GetTestData(0), GetTestData(1), GetTestData(2))

				responseMapper = GetTestDataMapper::class
			}
		}, processor)
	}

	private fun requestAssertion(methodSupplier: () -> GetMethod<List<GetTestData>>,
								 processor: (Expected<List<GetTestData>>) -> List<GetTestData>) {
		val methodBuilder = MethodBuilder(client, listType<GetTestData>())

		val method = methodSupplier()

		val expecteds = methodBuilder.build(method).let(processor)

		val okHttpClient = OkHttpClient()

		val request1 = Request.Builder().url("http://localhost:7000/main/0").build()
		val request2 = Request.Builder().url("http://localhost:7000/main/1").build()
		val request3 = Request.Builder().url("http://localhost:7000/main/2").build()

		val response1 = okHttpClient.newCall(request1).execute().let(GetTestDataMapper()::map)
		val response2 = okHttpClient.newCall(request2).execute().let(GetTestDataMapper()::map)
		val response3 = okHttpClient.newCall(request3).execute().let(GetTestDataMapper()::map)

		Assertions.assertEquals(expecteds, listOf(response1, response2, response3))
	}
}