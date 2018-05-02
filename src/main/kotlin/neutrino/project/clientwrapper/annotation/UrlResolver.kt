package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.resolver.url.UrlAnnotationResolver
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class UrlResolver(val urlRequestResolver: KClass<out UrlAnnotationResolver>)