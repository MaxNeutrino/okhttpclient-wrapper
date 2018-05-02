package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.resolver.url.GetUrlRequestResolver

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@UrlResolver(GetUrlRequestResolver::class)
annotation class GET(val url: String)