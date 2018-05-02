package neutrino.project.clientwrapper.frame.content

import okhttp3.Response

@FunctionalInterface
interface CountableLimit {
	fun isLimit(count: Int, response: Response): Boolean
}