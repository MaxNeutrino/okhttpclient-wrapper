package neutrino.project.clientwrapper.processor.response

import okhttp3.Response


interface ResponseProcessor {

	fun process(response: Response)
}