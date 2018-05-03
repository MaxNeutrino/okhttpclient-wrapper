package neutrino.project.clientwrapper.frame.converter

import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.frame.AbstractConverterTest
import neutrino.project.clientwrapper.frame.content.Content
import neutrino.project.clientwrapper.params

class DefaultRequestModelConverterTest : AbstractConverterTest() {

	override val paramsParams = params()

	override val mapParams = params()

	override val entityParams = params("id" to "content", "count" to "1", "property" to "")

	override val multiParams = entityParams

	private val converter = DefaultRequestModelConverter()

	override fun convert(content: Content): Params {
		return converter.convert(content)
	}
}