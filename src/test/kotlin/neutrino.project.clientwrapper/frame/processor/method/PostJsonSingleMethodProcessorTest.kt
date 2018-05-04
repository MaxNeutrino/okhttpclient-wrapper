package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.Client
import neutrino.project.clientwrapper.data.JsonTestConverter
import neutrino.project.clientwrapper.data.JsonTestData
import neutrino.project.clientwrapper.data.PostTestDataResponseMapper
import neutrino.project.clientwrapper.data.ResponsePostTestData
import neutrino.project.clientwrapper.frame.Expected
import neutrino.project.clientwrapper.frame.JsonPostMethod
import neutrino.project.clientwrapper.frame.MethodBuilder
import neutrino.project.clientwrapper.frame.content.Countable
import neutrino.project.clientwrapper.util.exception.JsonConverterNotFoundException
import neutrino.project.clientwrapper.util.ext.listType
import okhttp3.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class PostJsonSingleMethodProcessorTest : AbstractMethodProcessorTest() {

	private val client = Client.createSimple("http://localhost:7000")

	private val defaultJsonString = "{ 'hello' : 'world' }"

	@Test
	fun stringJsonProcessorTest() {
		val methodBuilder = MethodBuilder(client, String::class)

		val method = JsonPostMethod<String>().apply {
			url = "/main"
			json = defaultJsonString
		}

		val actual = methodBuilder.build(method).block()

		val okHttpClient = OkHttpClient.Builder().build()
		val request = Request.Builder()
				.url("http://localhost:7000/main")
				.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), defaultJsonString))
				.build()

		val expected = okHttpClient.newCall(request).execute().body()!!.string()

		Assertions.assertEquals(actual, expected)
		Assertions.assertEquals(actual, defaultJsonString)
	}

	@Test
	fun modelJsonProcessorTest() {
		val methodBuilder = MethodBuilder(client, String::class)

		val method = JsonPostMethod<String>().apply {
			url = "/main"
			jsonModel = JsonTestData("111", "Vasya")
			jsonConverter = JsonTestConverter::class
		}

		val actual = methodBuilder.build(method).block()

		val json = "{ id : 111, name : Vasya }"

		val okHttpClient = OkHttpClient.Builder().build()
		val request = Request.Builder()
				.url("http://localhost:7000/main")
				.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
				.build()

		val expected = okHttpClient.newCall(request).execute().body()!!.string()

		Assertions.assertEquals(actual, expected)
	}

	@Test
	fun mappedJsonModelTest() {
		mappedJsonModelAssertion { it.block() }
	}

	@Test
	fun asyncMappedJsonModelTest() {
		mappedJsonModelAssertion { it.async().get() }
	}

	@Test
	fun fakeAsyncMappedJsonModelTest() {
		mappedJsonModelAssertion { it.fakeAsync().get() }
	}

	@Test
	fun countableMappedJsonModelTest() {
		val methodBuilder = MethodBuilder(client, listType<ResponsePostTestData>())

		val method = JsonPostMethod<List<ResponsePostTestData>>().apply {
			url = "/main/count/json/{param}"

			pathCountable = Countable("param", 0, 1, { count, response -> count >= 2 })

			jsonModel = JsonTestData("111", "Vasya")
			jsonConverter = JsonTestConverter::class

			responseMapper = PostTestDataResponseMapper::class
		}

		val actual = methodBuilder.build(method).block()

		val json = "{ id : 111, name : Vasya }"

		val okHttpClient = OkHttpClient.Builder().build()
		val request1 = Request.Builder()
				.url("http://localhost:7000/main/count/json/0")
				.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
				.build()

		val expected1 = okHttpClient.newCall(request1).execute().let(PostTestDataResponseMapper()::map)

		val request2 = Request.Builder()
				.url("http://localhost:7000/main/count/json/1")
				.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
				.build()

		val expected2 = okHttpClient.newCall(request2).execute().let(PostTestDataResponseMapper()::map)

		Assertions.assertEquals(actual, listOf(expected1, expected2))
	}

	@Test
	fun jsonConverterNotFoundTest() {
		val methodBuilder = MethodBuilder(client, String::class)

		val method = JsonPostMethod<String>().apply {
			url = "/main"
			jsonModel = JsonTestData("111", "Vasya")
		}

		Assertions.assertThrows(JsonConverterNotFoundException::class.java, {
			val actual = methodBuilder.build(method).block()
			println(actual)
		})
	}

	private fun mappedJsonModelAssertion(processor: (Expected<ResponsePostTestData>) -> ResponsePostTestData) {
		val methodBuilder = MethodBuilder(client, ResponsePostTestData::class)

		val method = JsonPostMethod<ResponsePostTestData>().apply {
			url = "/main"
			jsonModel = JsonTestData("111", "Vasya")
			jsonConverter = JsonTestConverter::class

			responseMapper = PostTestDataResponseMapper::class
		}

		val actual = methodBuilder.build(method).let(processor)

		val json = "{ id : 111, name : Vasya }"

		val okHttpClient = OkHttpClient.Builder().build()
		val request = Request.Builder()
				.url("http://localhost:7000/main")
				.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
				.build()

		val expected = okHttpClient.newCall(request).execute().let(PostTestDataResponseMapper()::map)

		Assertions.assertEquals(actual, expected)
	}
}