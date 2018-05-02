package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.mapper.PaginatedResponseMapper
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Paginated(val paginatedMapper: KClass<out PaginatedResponseMapper<out Any>>)