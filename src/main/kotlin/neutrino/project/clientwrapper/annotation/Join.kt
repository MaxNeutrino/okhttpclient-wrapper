package neutrino.project.clientwrapper.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class Join(val separator: String)