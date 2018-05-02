package neutrino.project.clientwrapper.frame.content

import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.frame.converter.RequestModelConverter
import kotlin.reflect.KClass


data class Content(
		val name: String,
		val params: Params? = null,
		val map: Map<String, String>? = null,
		val model: Any? = null,
		var countable: Countable? = null,
		val modelConverter: KClass<out RequestModelConverter>? = null
) {
	fun isCountable() = countable != null

	fun isEmpty(): Boolean {
		return params == null
				&& map == null
				&& model == null
				&& countable == null
	}

	fun isNotEmpty(): Boolean = !isEmpty()
}