package neutrino.project.clientwrapper.frame.content

import okhttp3.Response


data class Countable(val name: String, var count: Int = 0, val step: Int = 1, val limit: (count: Int, response: Response?) -> Boolean) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Countable) return false

		if (name != other.name) return false
		if (count != other.count) return false
		if (step != other.step) return false

		return true
	}

	override fun hashCode(): Int {
		var result = name.hashCode()
		result = 31 * result + count
		result = 31 * result + step
		return result
	}
}

fun count(name: String, init: Int = 0, step: Int = 1, limit: (count: Int, response: Response?) -> Boolean): Countable {
	return Countable(name, init, step, limit)
}