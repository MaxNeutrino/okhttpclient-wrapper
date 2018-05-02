package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.mapper.ResponseMapper
import kotlin.reflect.KClass


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER,
		AnnotationTarget.PROPERTY_SETTER)
annotation class MapResponse(val mapper: KClass<out ResponseMapper<*>>)