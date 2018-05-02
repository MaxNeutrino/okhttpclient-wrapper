package neutrino.project.clientwrapper.frame.content

import okhttp3.Response


data class Countable(val name: String, var count: Int = 0, val step: Int = 1, val limit: (count: Int, response: Response?) -> Boolean)

fun count(name: String, init: Int = 0, step: Int = 1, limit: (count: Int, response: Response?) -> Boolean): Countable {
	return Countable(name, init, step, limit)
}