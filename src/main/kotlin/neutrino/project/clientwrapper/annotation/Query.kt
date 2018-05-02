package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.resolver.request.QueryMapResolver
import neutrino.project.clientwrapper.proxy.resolver.request.QueryModelResolver
import neutrino.project.clientwrapper.proxy.resolver.request.QueryParamsResolver
import neutrino.project.clientwrapper.proxy.resolver.request.QueryResolver

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE_PARAMETER)
@Resolver(QueryResolver::class)
annotation class Query(val name: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(QueryMapResolver::class)
annotation class QueryMap

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(QueryModelResolver::class)
annotation class QueryModel

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE_PARAMETER)
@Resolver(QueryParamsResolver::class)
annotation class QueryParams