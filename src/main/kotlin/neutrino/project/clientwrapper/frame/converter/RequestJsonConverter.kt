package neutrino.project.clientwrapper.frame.converter

interface RequestJsonConverter<in T> {
	fun convert(model: T): String
}