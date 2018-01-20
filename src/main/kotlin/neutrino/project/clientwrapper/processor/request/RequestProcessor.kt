package neutrino.project.clientwrapper.processor.request

import neutrino.project.clientwrapper.Client
import okhttp3.Request


interface RequestProcessor {

	fun process(client: Client, requestBuilder: Request.Builder): Request.Builder
}