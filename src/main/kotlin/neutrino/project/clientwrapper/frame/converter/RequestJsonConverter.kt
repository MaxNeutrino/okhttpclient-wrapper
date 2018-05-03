package neutrino.project.clientwrapper.frame.converter

interface RequestJsonConverter {
	fun convert(model: Any): String
}