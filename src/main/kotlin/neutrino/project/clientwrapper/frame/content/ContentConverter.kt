package neutrino.project.clientwrapper.frame.content

import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.frame.converter.DefaultRequestModelConverter
import neutrino.project.clientwrapper.params
import kotlin.reflect.full.primaryConstructor


class ContentConverter {

	private val defaultConverter = DefaultRequestModelConverter()

	fun convert(content: Content): Params {
		val params = content.params ?: params()

		if (content.map != null) {
			val mapParams = Params.from(content.map)
			params.addAll(mapParams)
		}

		if (content.model != null) {
			val converter = if (content.modelConverter != null) {
				content.modelConverter.primaryConstructor?.call()
			} else {
				defaultConverter
			}

			val modelParams = converter?.convert(content) ?: params()
			params.addAll(modelParams)
		}

		if (content.countable != null) {
			val name = content.countable!!.name
			val count = content.countable!!.count
			params.add(name to count.toString())
		}

		return params
	}
}