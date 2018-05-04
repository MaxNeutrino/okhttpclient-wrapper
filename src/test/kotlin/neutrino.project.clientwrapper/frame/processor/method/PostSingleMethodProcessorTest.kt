package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.data.CountableTestData
import neutrino.project.clientwrapper.data.PostTestData
import neutrino.project.clientwrapper.data.PostTestDataResponseMapper
import neutrino.project.clientwrapper.data.ResponsePostTestData
import neutrino.project.clientwrapper.frame.Expected
import neutrino.project.clientwrapper.frame.MethodBuilder
import neutrino.project.clientwrapper.frame.PostMethod
import neutrino.project.clientwrapper.util.ext.listType
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class PostSingleMethodProcessorTest : AbstractMethodProcessorTest() {

	private val client = Client.createSimple("http://localhost:7000")

	@Test
	fun modelResponseReceiveTest() {
		val methodBuilder = MethodBuilder(client, Response::class)

		val method = PostMethod<Response>().apply {
			url = "/main"
			bodyModel = PostTestData("123", "Logan")
		}

		val expected = methodBuilder.build(method).block()

		val okhttpClient = OkHttpClient.Builder().build()
		val request = Request.Builder()
				.url("http://localhost:7000/main")
				.post(FormBody.Builder().add("id", "123").add("name", "Logan").build())
				.build()

		val actual = okhttpClient.newCall(request).execute()

		assertResponses(expected, actual)
	}

	@Test
	fun modelReceiveTest() {
		modelReceive(Expected<ResponsePostTestData>::block)
	}

	@Test
	fun asyncModelReceiveTest() {
		modelReceive { it.async().get() }
	}

	@Test
	fun asyncFakeModelReceiveTest() {
		modelReceive { it.fakeAsync().get() }
	}

	@Test
	fun countableModelReceiveTest() {
		countableModelReceive { it.block() }
	}

	@Test
	fun asyncCountableModelReceiveTest() {
		countableModelReceive { it.async().get() }
	}

	@Test
	fun fakeAsyncCountableModelReceiveTest() {
		countableModelReceive { it.fakeAsync().get() }
	}

	@Test
	fun countableClassCastExceptionTest() {
		val methodBuilder = MethodBuilder(client, ResponsePostTestData::class)

		val method = PostMethod<ResponsePostTestData>().apply {
			url = "/main/count"
			bodyModel = CountableTestData("123", "Logan", 0)

			responseMapper = PostTestDataResponseMapper::class
		}

		Assertions.assertThrows(ClassCastException::class.java, {
			val response = methodBuilder.build(method).block()
			println(response.state)
		})
	}

	private fun modelReceive(processor: (Expected<ResponsePostTestData>) -> ResponsePostTestData) {
		val methodBuilder = MethodBuilder(client, ResponsePostTestData::class)

		val method = PostMethod<ResponsePostTestData>().apply {
			url = "/main"
			bodyModel = PostTestData("123", "Logan")

			responseMapper = PostTestDataResponseMapper::class
		}

		val expected = methodBuilder.build(method).let(processor)

		val okhttpClient = OkHttpClient.Builder().build()
		val request = Request.Builder()
				.url("http://localhost:7000/main")
				.post(FormBody.Builder().add("id", "123").add("name", "Logan").build())
				.build()

		val mapper = PostTestDataResponseMapper()
		val actual = okhttpClient.newCall(request).execute().let(mapper::map)

		Assertions.assertEquals(expected, actual)
	}

	private fun countableModelReceive(processor: (Expected<List<ResponsePostTestData>>) -> List<ResponsePostTestData>) {
		val methodBuilder = MethodBuilder(client, listType<ResponsePostTestData>())

		val method = PostMethod<List<ResponsePostTestData>>().apply {
			url = "/main/count"
			bodyModel = CountableTestData("123", "Logan", 0)

			responseMapper = PostTestDataResponseMapper::class
		}

		val expected = methodBuilder.build(method).let(processor)

		val okhttpClient = OkHttpClient.Builder().build()
		val actual0 = buildCountableRequest(okhttpClient, 0)
		val actual1 = buildCountableRequest(okhttpClient, 1)
		val actual2 = buildCountableRequest(okhttpClient, 2)

		Assertions.assertEquals(expected[0], actual0)
		Assertions.assertEquals(expected[1], actual1)
		Assertions.assertEquals(expected[2], actual2)
	}

	private fun buildCountableRequest(client: OkHttpClient, num: Int): ResponsePostTestData {
		val request = Request.Builder()
				.url("http://localhost:7000/main/count")
				.post(
						FormBody.Builder()
								.add("id", "123")
								.add("name", "Logan")
								.add("count", num.toString()
								).build())
				.build()

		return client.newCall(request).execute()
				.let(PostTestDataResponseMapper()::map)
	}
}