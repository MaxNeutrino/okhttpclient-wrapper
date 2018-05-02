package neutrino.project.clientwrapper.util.ext

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass


fun Any.findGenerics(): List<KClass<*>> {
	return this::class.findGenerics()
}

fun KClass<*>.findGenerics(): List<KClass<*>> {
	return this.java.findGenerics().map { it.kotlin }
}

fun Class<*>.findGenerics(): List<Class<*>> {
	val parameterizedType = this as ParameterizedType
	return parameterizedType.actualTypeArguments
			.map { it as Class<*> }
}

val Any.generic: KClass<*>
	get() = this::class.generic


val KClass<*>.generic: KClass<*>
	get() = this.java.generic.kotlin


val Class<*>.generic: Class<*>
	get() = this.findGenerics().first()

infix fun Any.subOf(model: Any): Boolean = model parentOf this

infix fun Any.subOf(type: KClass<*>): Boolean = type parentOf this

infix fun Any.subOf(type: Class<*>): Boolean = type parentOf this

infix fun KClass<*>.subOf(type: KClass<*>): Boolean = type parentOf this

infix fun Class<*>.subOf(type: KClass<*>): Boolean = type parentOf this

infix fun Class<*>.subOf(type: Class<*>): Boolean = type parentOf this

infix fun Any.parentOf(model: Any): Boolean = this::class parentOf model::class

infix fun KClass<*>.parentOf(type: KClass<*>): Boolean = this.java parentOf type.java

infix fun Class<*>.parentOf(type: Class<*>): Boolean = this.isAssignableFrom(type)

infix fun Class<*>.parentOf(type: KClass<*>): Boolean = this.isAssignableFrom(type.java)

infix fun Any.parentOf(type: KClass<*>): Boolean =this::class parentOf type

infix fun Any.parentOf(type: Class<*>): Boolean = this ::class.java parentOf type