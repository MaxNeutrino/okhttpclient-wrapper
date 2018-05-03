package neutrino.project.clientwrapper.frame.content

import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.frame.AbstractConverterTest
import neutrino.project.clientwrapper.params


class ContentConverterTest : AbstractConverterTest() {

	override val paramsParams = params("param1" to "path1")

	override val mapParams = params("param1" to "header1", "param2" to "header2")

	override val entityParams = params("id" to "content", "count" to "1", "property" to "")

	override val multiParams = params().also {
		it.addAll(paramsParams)
		it.addAll(mapParams)
		it.addAll(entityParams)
	}

	private val converter = ContentConverter()

	override fun convert(content: Content): Params {
		return converter.convert(content)
	}
}