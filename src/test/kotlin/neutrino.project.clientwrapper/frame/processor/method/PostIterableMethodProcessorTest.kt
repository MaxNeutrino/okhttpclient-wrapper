package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.data.CountableTestData
import neutrino.project.clientwrapper.data.PostTestData
import neutrino.project.clientwrapper.data.PostTestDataResponseMapper
import neutrino.project.clientwrapper.data.ResponsePostTestData
import neutrino.project.clientwrapper.frame.EmptyResponseMapper
import neutrino.project.clientwrapper.frame.Expected
import neutrino.project.clientwrapper.frame.MethodBuilder
import neutrino.project.clientwrapper.frame.PostMethod
import neutrino.project.clientwrapper.frame.content.Countable
import neutrino.project.clientwrapper.util.exception.CountableException
import neutrino.project.clientwrapper.util.exception.ResponseMapperNotFoundException
import neutrino.project.clientwrapper.util.ext.listType
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class PostIterableMethodProcessorTest: AbstractMethodProcessorTest() {

	private val client = Client.createSimple("http://localhost:7000")

	@Test
	fun receiveResponseTest() {
		val methodBuilder = MethodBuilder(client, listType<Response>())

		val method = PostMethod<List<Response>>().apply {
			url = "/main"
			bodyModel = listOf(PostTestData("1", "name1"), PostTestData("2", "name2"))

			responseMapper = EmptyResponseMapper::class
		}

		val okhttpClient = OkHttpClient.Builder().build()
		val request1 = Request.Builder()
				.url("http://localhost:7000/main")
				.post(FormBody.Builder().add("id", "1")
						.add("name", "name1").build())
				.build()

		val request2 = Request.Builder()
				.url("http://localhost:7000/main")
				.post(FormBody.Builder().add("id", "2")
						.add("name", "name2").build())
				.build()

		val response1 = okhttpClient.newCall(request1).execute()
		val response2 = okhttpClient.newCall(request2).execute()

		val actualList = methodBuilder.build(method).block()

		assertResponses(actualList[0], response1)
		assertResponses(actualList[1], response2)
	}

	@Test
	fun mappedModelReceiveTest() {
		val methodBuilder = MethodBuilder(client, listType<ResponsePostTestData>())

		val method = PostMethod<List<ResponsePostTestData>>().apply {
			url = "/main"
			bodyModel = listOf(PostTestData("1", "name1"), PostTestData("2", "name2"))

			responseMapper = PostTestDataResponseMapper::class
		}

		val okhttpClient = OkHttpClient.Builder().build()
		val request1 = Request.Builder()
				.url("http://localhost:7000/main")
				.post(FormBody.Builder().add("id", "1")
						.add("name", "name1").build())
				.build()

		val request2 = Request.Builder()
				.url("http://localhost:7000/main")
				.post(FormBody.Builder().add("id", "2")
						.add("name", "name2").build())
				.build()

		val mapper = PostTestDataResponseMapper()
		val response1 = okhttpClient.newCall(request1).execute().let { mapper.map(it) }
		val response2 = okhttpClient.newCall(request2).execute().let { mapper.map(it) }

		val actualList = methodBuilder.build(method).block()

		Assertions.assertEquals(actualList, listOf(response1, response2))
	}

	@Test
	fun asyncMappedModelReceiveTest() {
		val methodBuilder = MethodBuilder(client, listType<ResponsePostTestData>())

		val method = PostMethod<List<ResponsePostTestData>>().apply {
			url = "/main"
			bodyModel = listOf(PostTestData("1", "name1"), PostTestData("2", "name2"))

			responseMapper = PostTestDataResponseMapper::class
		}

		val okhttpClient = OkHttpClient.Builder().build()
		val request1 = Request.Builder()
				.url("http://localhost:7000/main")
				.post(FormBody.Builder().add("id", "1")
						.add("name", "name1").build())
				.build()

		val request2 = Request.Builder()
				.url("http://localhost:7000/main")
				.post(FormBody.Builder().add("id", "2")
						.add("name", "name2").build())
				.build()

		val mapper = PostTestDataResponseMapper()

		val response1 = okhttpClient.newCall(request1).execute().let { mapper.map(it) }
		val response2 = okhttpClient.newCall(request2).execute().let { mapper.map(it) }

		val actualList = methodBuilder.build(method)
				.async()
				.get()
				.sortedBy { it.state }

		Assertions.assertEquals(actualList, listOf(response1, response2))
	}

	@Test
	fun fakeAsyncMappedModelReceiveTest() {
		val methodBuilder = MethodBuilder(client, listType<ResponsePostTestData>())

		val method = PostMethod<List<ResponsePostTestData>>().apply {
			url = "/main"
			bodyModel = listOf(PostTestData("1", "name1"), PostTestData("2", "name2"))

			responseMapper = PostTestDataResponseMapper::class
		}

		val okhttpClient = OkHttpClient.Builder().build()
		val request1 = Request.Builder()
				.url("http://localhost:7000/main")
				.post(FormBody.Builder().add("id", "1")
						.add("name", "name1").build())
				.build()

		val request2 = Request.Builder()
				.url("http://localhost:7000/main")
				.post(FormBody.Builder().add("id", "2")
						.add("name", "name2").build())
				.build()

		val mapper = PostTestDataResponseMapper()

		val response1 = okhttpClient.newCall(request1).execute().let { mapper.map(it) }
		val response2 = okhttpClient.newCall(request2).execute().let { mapper.map(it) }

		val actualList = methodBuilder.build(method)
				.fakeAsync()
				.get()
				.sortedBy { it.state }

		Assertions.assertEquals(actualList, listOf(response1, response2))
	}

	@Test
	fun countableModelReceiveTest() {
		countablePostTest { it.block() }
	}

	@Test
	fun asyncCountableModelReceiveTest() {
		countablePostTest { it.async().get() }
	}

	@Test
	fun fakeAsyncCountableModelReceiveTest() {
		countablePostTest { it.fakeAsync().get() }
	}

	@Test
	fun mapperNotFoundTest() {
		val methodBuilder = MethodBuilder(client, listType<ResponsePostTestData>())

		val method = PostMethod<List<ResponsePostTestData>>().apply {
			url = "/main"
			bodyModel = listOf(PostTestData("1", "name1"), PostTestData("2", "name2"))
		}

		Assertions.assertThrows(ResponseMapperNotFoundException::class.java) {
			val response = methodBuilder.build(method).block()
			println(response)
		}
	}

	@Test
	fun countableExceptionTest() {
		val methodBuilder = MethodBuilder(client, listType<ResponsePostTestData>())

		val method = PostMethod<List<ResponsePostTestData>>().apply {
			url = "/main/count"
			pathCountable = Countable("fakeCount", 0, 4, { c, _ -> c >= 10 })
			bodyModel = listOf(CountableTestData("1", "name1", 0), CountableTestData("2", "name2", 0))

			responseMapper = PostTestDataResponseMapper::class
		}

		Assertions.assertThrows(CountableException::class.java) {
			val response = methodBuilder.build(method).block()
			println(response)
		}
	}

	private fun countablePostTest(processor: (Expected<List<ResponsePostTestData>>) -> List<ResponsePostTestData>) {
		val methodBuilder = MethodBuilder(client, listType<ResponsePostTestData>())

		val method = PostMethod<List<ResponsePostTestData>>().apply {
			url = "/main/count"
			bodyModel = listOf(CountableTestData("1", "name1", 0), CountableTestData("2", "name2", 0))

			responseMapper = PostTestDataResponseMapper::class
		}

		val expected = methodBuilder.build(method).let(processor)

		val okhttpClient = OkHttpClient.Builder().build()
		val actual0 = buildCountableRequest(okhttpClient, 0, "1", "name1")
		val actual1 = buildCountableRequest(okhttpClient, 1, "1", "name1")
		val actual2 = buildCountableRequest(okhttpClient, 2, "1", "name1")
		val actual3 = buildCountableRequest(okhttpClient, 0, "2", "name2")
		val actual4 = buildCountableRequest(okhttpClient, 1, "2", "name2")
		val actual5 = buildCountableRequest(okhttpClient, 2, "2", "name2")

		Assertions.assertEquals(expected[0], actual0)
		Assertions.assertEquals(expected[1], actual1)
		Assertions.assertEquals(expected[2], actual2)
		Assertions.assertEquals(expected[3], actual3)
		Assertions.assertEquals(expected[4], actual4)
		Assertions.assertEquals(expected[5], actual5)
	}

	private fun buildCountableRequest(client: OkHttpClient, num: Int, id: String, name: String): ResponsePostTestData {
		val request = Request.Builder()
				.url("http://localhost:7000/main/count")
				.post(
						FormBody.Builder()
								.add("id", id)
								.add("name", name)
								.add("count", num.toString()
								).build())
				.build()

		return client.newCall(request).execute()
				.let(PostTestDataResponseMapper()::map)
	}
}