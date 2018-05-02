package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.Params
import neutrino.project.clientwrapper.frame.*
import neutrino.project.clientwrapper.frame.content.Content
import neutrino.project.clientwrapper.util.exception.CountableException


abstract class AbstractRequestMethodProcessor<T : Any>(
		protected val isCountableEnabled: Boolean) : RequestMethodProcessor<T>, Interruptable {

	protected val contentModifications: Map<String, (Params, RequestMethodModel) -> RequestMethodModel> = mapOf(
			"queries" to ::processQuery,
			"path" to ::processPath,
			"body" to ::processBody,
			"headers" to ::processHeaders
	)

	protected fun getContentNames() = contentModifications.keys

	protected fun findCountable(contents: Collection<Content>): Content? {
		val countableContent = contents.filter { it.countable != null }

		if (!isCountableEnabled)
			return null

		return when {
			countableContent.size > 1 -> throw CountableException("Too many countable fields")
			countableContent.size == 1 -> countableContent.first()
			else -> null
		}
	}
}