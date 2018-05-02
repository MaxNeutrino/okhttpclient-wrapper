package neutrino.project.clientwrapper.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
annotation class RootUrl(val url: String)