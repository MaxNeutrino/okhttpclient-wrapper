package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.resolver.RequestResolver
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class Resolver(val requestResolver: KClass<out RequestResolver>)