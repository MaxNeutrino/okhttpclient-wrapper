package neutrino.project.clientwrapper.frame.content

import neutrino.project.clientwrapper.frame.converter.RequestJsonConverter
import neutrino.project.clientwrapper.util.exception.JsonConverterNotFoundException
import neutrino.project.clientwrapper.util.exception.RequestMethodException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.reflect.full.declaredMemberProperties


class JsonContentConverterTest {

	private val converter = JsonContentConverter()

	private val stringJsonContent = JsonContent(
			json = "{ 'hello' : 'world' }"
	)

	private val expectedJsonString = "{ 'hello' : 'world' }"

	private val modelJsonContent = JsonContent(
			jsonModel = MockJsonModel(),
			jsonConverter = MockJsonConverter::class
	)

	private val expectedJsonModel = "{ param1 : value1, param2 : value2 }"

	private val stringModelJsonContent = JsonContent(
			json = "{ 'hello' : 'world' }",
			jsonModel = MockJsonModel(),
			jsonConverter = MockJsonConverter::class
	)

	private val modelWthoutConverterContent = JsonContent(
			jsonModel = MockJsonModel()
	)

	@Test
	fun stringJsonConverterTest() {
		val actual = converter.convert(stringJsonContent)
		Assertions.assertEquals(expectedJsonString, actual)
	}

	@Test
	fun modelJsonConverterTest() {
		val actual = converter.convert(modelJsonContent)
		Assertions.assertEquals(expectedJsonModel, actual)
	}

	@Test
	fun stringAndModelConverterExceptionTest() {
		Assertions.assertThrows(RequestMethodException::class.java, { converter.convert(stringModelJsonContent) })
	}

	@Test
	fun converterNotFoundTest() {
		Assertions.assertThrows(JsonConverterNotFoundException::class.java,
				{ converter.convert(modelWthoutConverterContent) })
	}


}

data class MockJsonModel(val param1: String = "value1", val param2: String = "value2")

class MockJsonConverter : RequestJsonConverter {
	override fun convert(model: Any): String {
		val json = model::class.declaredMemberProperties.joinToString(", ") {
			val name = it.name
			val value = it.getter.call(model) as String
			"$name : $value"
		}

		return "{ $json }"
	}
}