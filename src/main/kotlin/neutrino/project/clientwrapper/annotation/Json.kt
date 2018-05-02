package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.resolver.request.JsonObjectResolver
import neutrino.project.clientwrapper.proxy.resolver.request.JsonResolver

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(JsonResolver::class)
annotation class Json

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(JsonObjectResolver::class)
annotation class JsonModel