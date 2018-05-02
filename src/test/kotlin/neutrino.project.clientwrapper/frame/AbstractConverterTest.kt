package neutrino.project.clientwrapper.frame

import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.annotation.Count
import neutrino.project.clientwrapper.frame.content.Content
import neutrino.project.clientwrapper.frame.content.CountableLimit
import neutrino.project.clientwrapper.params
import neutrino.project.clientwrapper.util.exception.CountableException
import okhttp3.Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


abstract class AbstractConverterTest {
	private val paramsContent = Content(
			name = "path",
			params = params("param1" to "path1")
	)

	abstract val paramsParams: Params

	private val mapContent = Content(
			name = "headers",
			map = mapOf("param1" to "header1", "param2" to "header2")
	)

	abstract val mapParams: Params
	private val entityContent = Content(
			name = "body",
			model = EntityContent()
	)

	abstract val entityParams: Params

	private val multiContent = Content(
			name = "body",
			params = params("param1" to "path1"),
			map = mapOf("param1" to "header1", "param2" to "header2"),
			model = EntityContent()
	)

	abstract val multiParams: Params

	private val excpetionalContent = Content(
			name = "body",
			model = CountableEntityContent()
	)

	@Test
	fun convertParamsContentTest() {
		convert(paramsContent, paramsParams)
	}

	@Test
	fun convertMapContentTest() {
		convert(mapContent, mapParams)
	}

	@Test
	fun convertEntityContentTest() {
		convert(entityContent, entityParams)
	}

	@Test
	fun convertMultiContentTest() {
		convert(multiContent, multiParams)
	}

	@Test
	fun countableExceptionTest() {
		Assertions.assertThrows(CountableException::class.java, { convert(excpetionalContent, params()) })
	}

	abstract fun convert(content: Content): Params

	private fun convert(content: Content, expectedResult: Params) {
		val converted = convert(content)
		val actualCollection = converted.getParams().sortedBy { it.first }
		val expectedCollection = expectedResult.getParams().sortedBy { it.first }

		Assertions.assertEquals(expectedCollection, actualCollection)
	}

	data class EntityContent(val id: String = "content", val count: Int = 1, val property: String? = null)

	data class CountableEntityContent(val id: String = "content", @Count(step = 15,
			limit = Limit::class) val count: Int = 1, @Count(step = 15, limit = Limit::class) val secondCount: Int = 2,
									  val property: String? = null)
}

class Limit : CountableLimit {
	override fun isLimit(count: Int, response: Response?): Boolean {
		return true
	}
}