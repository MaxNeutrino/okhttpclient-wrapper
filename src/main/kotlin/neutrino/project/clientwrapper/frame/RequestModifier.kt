package neutrino.project.clientwrapper.frame

import neutrino.project.clientwrapper.Params
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.RequestBody


fun processQuery(params: Params, requestMethodModel: RequestMethodModel): RequestMethodModel {
	var url = requestMethodModel.getUrl()
	val query = params.joinToString("&") { "${it.first}=${it.second}" }
	url = "$url?$query"
	requestMethodModel.setUrl(url)
	return requestMethodModel
}

fun processPath(params: Params, requestMethodModel: RequestMethodModel): RequestMethodModel {
	var url = requestMethodModel.getUrl()

	params.forEach {
		val name = it.first
		val value = it.second
		url = url.replace("{$name}", value)
	}
	requestMethodModel.setUrl(url)
	return requestMethodModel
}

fun processHeaders(params: Params, requestMethodModel: RequestMethodModel): RequestMethodModel {
	params.forEach {
		val name = it.first
		val value = it.second

		requestMethodModel.requestBuilder.addHeader(name, value)
	}
	return requestMethodModel
}

fun processBody(params: Params, requestMethodModel: RequestMethodModel): RequestMethodModel {
	val requestBodyBuilder = FormBody.Builder()
	params.forEach {
		val name = it.first
		val value = it.second

		requestBodyBuilder.add(name, value)
	}
	requestMethodModel.requestBody = requestBodyBuilder.build()
	return requestMethodModel
}

fun processJson(json: String, requestMethodModel: RequestMethodModel): RequestMethodModel {
	val jsonMediaType = MediaType.parse("application/json; charset=utf-8")
	val requestBody = RequestBody.create(jsonMediaType, json)
	requestMethodModel.requestBody = requestBody
	return requestMethodModel
}

fun processJson(json: Params, requestMethodModel: RequestMethodModel): RequestMethodModel {
	if (json.size > 1)
		throw IllegalArgumentException("Invalid json")

	val jsonString = json.first().second
	return processJson(jsonString, requestMethodModel)
}
