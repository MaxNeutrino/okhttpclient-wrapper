package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.resolver.request.PathMapResolver
import neutrino.project.clientwrapper.proxy.resolver.request.PathModelResolver
import neutrino.project.clientwrapper.proxy.resolver.request.PathParamsResolver
import neutrino.project.clientwrapper.proxy.resolver.request.PathResolver

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(PathResolver::class)
annotation class Path(val name: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(PathParamsResolver::class)
annotation class PathParams

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(PathMapResolver::class)
annotation class PathMap

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Resolver(PathModelResolver::class)
annotation class PathModel