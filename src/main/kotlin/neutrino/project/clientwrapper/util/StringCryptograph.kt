package neutrino.project.clientwrapper.util


interface StringCryptograph {

	fun encrypt(field: String): String

	fun decrypt(field: String): String
}