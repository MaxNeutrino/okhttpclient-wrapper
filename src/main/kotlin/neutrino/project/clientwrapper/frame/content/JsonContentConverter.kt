package neutrino.project.clientwrapper.frame.content

import neutrino.project.clientwrapper.util.exception.JsonConverterNotFoundException
import neutrino.project.clientwrapper.util.exception.RequestMethodException
import kotlin.reflect.full.primaryConstructor


class JsonContentConverter {
	fun convert(content: JsonContent): String? {
		return when {
			content.json != null && content.jsonModel != null -> throw RequestMethodException(
					"Cont apply json string and json model")
			content.jsonModel != null && content.jsonConverter == null -> throw JsonConverterNotFoundException(
					"for model ${content.jsonModel}")
			content.jsonModel != null -> {
				val converter = content.jsonConverter?.primaryConstructor?.call()
						?: throw JsonConverterNotFoundException("for model ${content.jsonModel}")
				converter.convert(content.jsonModel!!)
			}
			content.json != null -> content.json!!
			else -> null
		}
	}
}