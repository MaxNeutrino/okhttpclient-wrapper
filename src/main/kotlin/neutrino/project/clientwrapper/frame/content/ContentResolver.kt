package neutrino.project.clientwrapper.frame.content

import neutrino.project.clientwrapper.frame.RequestMethod
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor


interface ContentResolver {
	fun resolve(method: RequestMethod<out Any>): Content?
}

class ReflectiveContentResolver(private val nameContaining: String) : ContentResolver {

	override fun resolve(method: RequestMethod<out Any>): Content? {
		val methodClass = method::class
		val fields = methodClass.declaredMemberProperties
				.filter { it.name.contains(nameContaining) }

		if (fields.isEmpty())
			return null

		val objects = fields.map { it.getter.call(method) }

		return Content::class.primaryConstructor!!.call(nameContaining, *objects.toTypedArray())
	}
}