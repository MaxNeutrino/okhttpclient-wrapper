package neutrino.project.clientwrapper.storage

import java.io.File


class DefaultStorageProvider : StorageProvider {

	private val userHome = System.getProperty("user.home")

	override val cookieDir by lazy {
		val dir = File(userHome, ".cookie")
		if (!dir.exists())
			dir.mkdirs()
		dir
	}


	override val cacheDir by lazy {
		val dir = File(userHome, ".cache")
		if (!dir.exists())
			dir.mkdirs()
		dir
	}
}