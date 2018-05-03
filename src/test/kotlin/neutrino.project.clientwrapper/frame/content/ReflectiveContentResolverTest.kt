package neutrino.project.clientwrapper.frame.content

import neutrino.project.clientwrapper.frame.PostMethod
import neutrino.project.clientwrapper.params
import okhttp3.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime


class ReflectiveContentResolverTest {

	private val method = PostMethod<Response>()

	private val time = LocalDateTime.now()

	init {
		with(method) {
			queriesParams = params("first" to "value")
			queriesCountable = Countable("awesomeQuery", 1, 15, { _, _ -> true })
			pathMap = mapOf("user" to "userId", "username" to "qwerty")
			headersParams = params("name" to "header")
			bodyModel = ReflectiveResolverEntity(
					time = time)
		}
	}

	private val queriesContent = Content(
			name = "queries",
			params = params("first" to "value"),
			countable = Countable("awesomeQuery", 1, 15, { _, _ -> true })
	)

	private val pathContent = Content(
			name = "path",
			map = mapOf("user" to "userId", "username" to "qwerty")
	)

	private val headersContent = Content(
			name = "headers",
			params = params("name" to "header")
	)

	private val bodyContent = Content(
			name = "body",
			model = ReflectiveResolverEntity(
					time = time)
	)

	@Test
	fun queriesContentTest() {
		resolve(queriesContent.name, queriesContent)
	}

	@Test
	fun pathContentTest() {
		resolve(pathContent.name, pathContent)
	}

	@Test
	fun headersContentTest() {
		resolve(headersContent.name, headersContent)
	}

	@Test
	fun bodyContentTest() {
		resolve(bodyContent.name, bodyContent)
	}

	private fun resolve(name: String, expected: Content) {
		val resolver = ReflectiveContentResolver(name)
		val actual = resolver.resolve(method)

		assertEquals(expected, actual)
	}

	data class ReflectiveResolverEntity(val param1: String = "value1",
										val param2: String = "value2",
										val time: LocalDateTime,
										val list: List<String> = listOf("good", "bad"),
										val person: Person = Person()
	)

	data class Person(val name: String = "myname", val surname: String = "mysurname")
}