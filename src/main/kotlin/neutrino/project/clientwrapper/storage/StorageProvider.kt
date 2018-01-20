package neutrino.project.clientwrapper.storage

import java.io.File


interface StorageProvider {

	val cacheDir: File

	val cookieDir: File
}