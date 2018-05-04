package neutrino.project.clientwrapper.util.ext

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

fun genericByFunction(func: KFunction<*>): KClass<out Any> {
	return func.returnType as KClass<*>
}


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

infix fun Any.parentOf(type: KClass<*>): Boolean = this::class parentOf type

infix fun Any.parentOf(type: Class<*>): Boolean = this::class.java parentOf type

fun <T: Any> listType(): KClass<out List<T>> {
	return listOf<T>()::class
}