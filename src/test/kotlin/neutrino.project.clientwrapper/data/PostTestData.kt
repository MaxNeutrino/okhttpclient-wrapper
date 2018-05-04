package neutrino.project.clientwrapper.data

import neutrino.project.clientwrapper.annotation.Count
import neutrino.project.clientwrapper.frame.ResponseMapper
import neutrino.project.clientwrapper.frame.content.CountableLimit
import okhttp3.Response

const val ID_POST_PARAM_NAME = "id"
const val NAME_POST_PARAM_NAME = "name"
const val COUNT_POST_PARAM_NAME = "count"

data class PostTestData(val id: String, val name: String)

data class ResponsePostTestData(val state: String)

class PostTestDataResponseMapper: ResponseMapper<ResponsePostTestData> {
	override fun map(response: Response): ResponsePostTestData {
		return ResponsePostTestData(response.body()!!.string())
	}
}

data class CountableTestData(val id: String, val name: String, @Count(step = 1, limit = CountableTestDataLimit::class) val count: Int = 0)

class CountableTestDataLimit : CountableLimit {
	override fun isLimit(count: Int, response: Response?): Boolean {
		return (response!!.code() == 404 || response.code() == 500)
				.also { response.close() }
	}
}