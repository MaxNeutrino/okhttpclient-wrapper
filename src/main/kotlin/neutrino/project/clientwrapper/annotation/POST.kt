package neutrino.project.clientwrapper.annotation

import neutrino.project.clientwrapper.proxy.resolver.url.PostUrlRequestResolver

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@UrlResolver(PostUrlRequestResolver::class)
annotation class POST(val url: String)