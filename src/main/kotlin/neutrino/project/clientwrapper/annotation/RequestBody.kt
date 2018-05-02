package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.resolver.request.ParamResolver
import neutrino.project.clientwrapper.proxy.resolver.request.RequestMapResolver
import neutrino.project.clientwrapper.proxy.resolver.request.RequestModelResolver
import neutrino.project.clientwrapper.proxy.resolver.request.RequestParamsResolver


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(RequestModelResolver::class)
annotation class RequestModel

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(RequestParamsResolver::class)
annotation class RequestParams

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(RequestMapResolver::class)
annotation class RequestMap

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(ParamResolver::class)
annotation class Param(val name: String)