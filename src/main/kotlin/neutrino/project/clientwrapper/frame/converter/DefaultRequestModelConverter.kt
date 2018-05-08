package neutrino.project.clientwrapper.frame.converter

import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.annotation.Count
import neutrino.project.clientwrapper.annotation.ParamName
import neutrino.project.clientwrapper.frame.content.Content
import neutrino.project.clientwrapper.frame.content.Countable
import neutrino.project.clientwrapper.params
import neutrino.project.clientwrapper.util.exception.CountableException
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField


class DefaultRequestModelConverter(
		dateTimePattern: String = "yyyy-MM-dd hh:mm") : RequestModelConverter {

	private val formatter = DateTimeFormatter.ofPattern(dateTimePattern)

	@Throws(CountableException::class)
	override fun convert(content: Content): Params {
		val model = content.model ?: return params()
		val properties = model::class.declaredMemberProperties

		val countAnnotated = properties.filter {
			it.annotations.find { it is Count } != null
		}

		if (countAnnotated.size > 1)
			throw CountableException("Too many countable params")

		val paramPairs = properties.mapNotNull { property ->
			val isCountable = processCountable(content, property)

			if (isCountable)
				return@mapNotNull null

			if (isCollection(property))
				return@mapNotNull null

			val name = getName(property)

			val value = property.getter.call(model)
			val stringValue = resolveString(value)

			name to stringValue
		}

		val collectionsParams = properties.filter { isCollection(it) }
				.flatMap { property ->
					val name = getName(property)

					val collection = resolveCollection(property, model)
					collection.map {
						name to resolveString(it)
					}
				}

		val params = params(*paramPairs.toTypedArray())
		params.addAll(*collectionsParams.toTypedArray())

		return params
	}

	private fun resolveString(obj: Any?): String {
		return when {
			obj == null -> ""
			String::class.java.isAssignableFrom(obj::class.java) -> obj as String
			TemporalAccessor::class.java.isAssignableFrom(obj::class.java) -> formatter.format(obj as TemporalAccessor)
			else -> obj.toString()
		}
	}

	private fun resolveCollection(property: KProperty1<*, Any?>, model: Any): Collection<Any?> {
		val obj = property.getter.call(model)
		obj ?: return emptyList()
		return obj as Collection<Any?>
	}

	private fun isCollection(property: KProperty1<*, Any?>): Boolean {
		val javaField = property.javaField
		val type = javaField?.type ?: return false
		return Collection::class.java.isAssignableFrom(type)
	}

	@Throws(CountableException::class)
	private fun processCountable(content: Content, property: KProperty1<*, Any?>): Boolean {
		val countAnnotation = property.annotations.find { it is Count }
		return if (countAnnotation != null) {
			if (content.countable != null)
				throw CountableException("Too many countable params")

			val value = property.getter.call(content.model) ?: return false
			val name = getName(property)
			val num = value as Int

			val annotation = property.annotations.first { it is Count } as Count/*javaField.getAnnotation(Count::class.java)*/

			val step = annotation.step
			val stopper = annotation.limit
					.primaryConstructor
					?.call() ?: return false

			content.countable = Countable(
					name = name,
					count = num,
					step = step,
					limit = stopper::isLimit
			)

			true
		} else {
			false
		}
	}

	private fun getName(property: KProperty1<*, Any?>): String {
		val javaField = property.javaField
		return if (javaField?.isAnnotationPresent(ParamName::class.java) == true) {
			javaField.getAnnotation(ParamName::class.java).name
		} else {
			property.name
		}
	}
}