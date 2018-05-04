package neutrino.project.clientwrapper.data

import neutrino.project.clientwrapper.frame.converter.RequestJsonConverter


data class JsonTestData(val id: String, val name: String)

class JsonTestConverter: RequestJsonConverter {
	override fun convert(model: Any): String {
		model as JsonTestData
		return "{ id : ${model.id}, name : ${model.name} }"
	}
}