package neutrino.project.clientwrapper


class Params private constructor(
		private var params: MutableList<Pair<String, String>>) : MutableCollection<Pair<String, String>> {

	override val size: Int
		get() = params.size

	override fun contains(element: Pair<String, String>) = params.contains(element)


	override fun containsAll(elements: Collection<Pair<String, String>>) = params.containsAll(elements)


	override fun addAll(elements: Collection<Pair<String, String>>) = this.params.addAll(elements)


	override fun clear() {
		params = mutableListOf()
	}

	override fun remove(element: Pair<String, String>) = params.remove(element)

	override fun removeAll(elements: Collection<Pair<String, String>>) = params.removeAll(elements)

	override fun retainAll(elements: Collection<Pair<String, String>>) = params.retainAll(elements)

	override fun add(element: Pair<String, String>) = params.add(element)

	fun replace(element: Pair<String, String>) {
		val containedElement = params.filter { it.first == element.first }

		if (containedElement.isNotEmpty()) {
			containedElement.forEach {
				params.remove(it)
				add(element)
			}
		} else {
			add(element)
		}
	}

	fun addAll(params: Params) {
		this.params.addAll(params.getParams())
	}

	fun addAll(vararg params: Pair<String, String>) {
		this.params.addAll(params)
	}

	fun getParams(): List<Pair<String, String>> {
		return params
	}

	override fun isEmpty() = params.size == 0

	fun isNotEmpty() = !isEmpty()

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Params) return false

		if (params != other.params) return false

		return true
	}

	override fun hashCode(): Int {
		return params.hashCode()
	}

	override fun toString(): String {
		return "Params(params=$params)"
	}

	companion object {
		fun of(vararg params: Pair<String, String>): Params {
			return Params(params.toMutableList())
		}

		fun from(map: Map<String, String>): Params {
			return Params(map.map { it.toPair() }.toMutableList())
		}
	}

	override fun iterator(): MutableIterator<Pair<String, String>> {
		return params.iterator()
	}
}

fun params(vararg params: Pair<String, String>): Params {
	return Params.of(*params)
}