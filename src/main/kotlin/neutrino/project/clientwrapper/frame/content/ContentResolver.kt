package neutrino.project.clientwrapper.frame.content

import neutrino.project.clientwrapper.frame.JsonPostMethod
import neutrino.project.clientwrapper.frame.RequestMethod
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor


interface ContentResolver {
	fun resolve(method: RequestMethod<out Any>): Content?
}

class ReflectiveContentResolver(private val nameContaining: String) : ContentResolver {

	override fun resolve(method: RequestMethod<out Any>): Content? {
		val methodClass = method::class
		val fields = methodClass.memberProperties
				.filter { it.name.contains(nameContaining) }

		if (fields.isEmpty())
			return null

		val objects = fields.map { it.name to it.getter.call(method) }
				.toMap()

		val primaryConstructor = Content::class.primaryConstructor!!
		val params = primaryConstructor.parameters

		val objectNames = objects.keys
		val args = params.map { param ->
			if (param.name == "name") {
				nameContaining
			} else {
				val name = objectNames.filter {
					it.contains(param.name ?: "", true)
				}.first()
				objects[name]
			}
		}.toTypedArray()

		return Content::class.primaryConstructor!!.call(*args)
	}
}


fun jsonContentResolver(method: JsonPostMethod<out Any>): JsonContent {
	return JsonContent(
			method.json,
			method.jsonModel,
			method.jsonConverter
	)
}