package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.frame.content.CountableLimit
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Count(val step: Int = 1, val limit: KClass<out CountableLimit>)