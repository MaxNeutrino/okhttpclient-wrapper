package neutrino.project.clientwrapper.util.cookie

import neutrino.project.clientwrapper.util.StringCryptograph


class CookieCryptograph(private val fieldCryptograph: StringCryptograph) {

	fun encrypt(cookieTo: CookieTo): CookieTo {
		val name = fieldCryptograph.encrypt(cookieTo.name)
		val value = fieldCryptograph.encrypt(cookieTo.value)
		val domain = fieldCryptograph.encrypt(cookieTo.domain)

		return CookieTo(name, value, domain)
	}

	fun decrypt(cookieTo: CookieTo): CookieTo {
		val name = fieldCryptograph.decrypt(cookieTo.name)
		val value = fieldCryptograph.decrypt(cookieTo.value)
		val domain = fieldCryptograph.decrypt(cookieTo.domain)

		return CookieTo(name, value, domain)
	}
}