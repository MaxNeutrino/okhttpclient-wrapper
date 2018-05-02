package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.resolver.request.HeaderMapResolver
import neutrino.project.clientwrapper.proxy.resolver.request.HeaderModelResolver
import neutrino.project.clientwrapper.proxy.resolver.request.HeaderParamsResolver
import neutrino.project.clientwrapper.proxy.resolver.request.HeaderResolver

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(HeaderResolver::class)
annotation class Header(val name: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(HeaderParamsResolver::class)
annotation class HeaderParams

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(HeaderMapResolver::class)
annotation class HeadersMap

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(HeaderModelResolver::class)
annotation class HeadersModel