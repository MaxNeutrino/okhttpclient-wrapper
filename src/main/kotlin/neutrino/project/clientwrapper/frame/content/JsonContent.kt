package neutrino.project.clientwrapper.frame.content

import neutrino.project.clientwrapper.frame.converter.RequestJsonConverter
import kotlin.reflect.KClass


data class JsonContent(
		var json: String? = null,
		var jsonModel: Any? = null,
		var jsonConverter: KClass<out RequestJsonConverter>? = null
)
