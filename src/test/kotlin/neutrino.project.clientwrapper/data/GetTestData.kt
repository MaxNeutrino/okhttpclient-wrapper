package neutrino.project.clientwrapper.data

import neutrino.project.clientwrapper.frame.ResponseMapper
import okhttp3.Response


data class GetTestData(val param: Int = 1)

class GetTestDataMapper : ResponseMapper<GetTestData> {
	override fun map(response: Response): GetTestData {
		val body = response.body()!!.string()!!.toInt()
		return GetTestData(body)
	}
}