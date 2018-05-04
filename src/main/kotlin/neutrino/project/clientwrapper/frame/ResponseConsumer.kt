package neutrino.project.clientwrapper.frame

import neutrino.project.clientwrapper.util.exception.RequestMethodException
import okhttp3.Call
import okhttp3.Response


data class ResponseConsumer<T>(
		val call: Call? = null,
		val response: Response? = null,
		var responses: MutableList<Response>? = null,
		val responseModel: T? = null,
		var responseModels: MutableList<T>? = null
) {

	@Suppress("UNCHECKED_CAST")
	fun getResult(): T {
		val result = when {
			call != null -> call
			response != null -> response
			responses != null && responses?.isNotEmpty() == true -> responses
			responseModel != null -> responseModel
			responseModels != null && responseModels?.isNotEmpty() == true -> responseModels
			else -> throw RequestMethodException("ResponseConsumer is empty")
		}!!

		/*// TODO change with findGeneric
		val returnable = *//*this.findGenerics().first()*//* this::class

		if (!(returnable parentOf result::class))
			throw TypeNotFoundException("Expected ${returnable.qualifiedName} actual ${result::class.qualifiedName}")*/

		return result as T
	}

	fun merge(responseConsumer: ResponseConsumer<T>) {
		if (responses == null) {
			responses = mutableListOf()
			if (response != null) responses!!.add(response)
		}

		if (responseModels == null) {
			responseModels = mutableListOf()
			if (responseModel != null) responseModels!!.add(responseModel)
		}

		if (responseConsumer.response != null) responses!!.add(responseConsumer.response)
		if (responseConsumer.responses != null) responses!!.addAll(responseConsumer.responses!!)
		if (responseConsumer.responseModel != null) responseModels!!.add(responseConsumer.responseModel)
		if (responseConsumer.responseModels != null) responseModels!!.addAll(responseConsumer.responseModels!!)
	}
}