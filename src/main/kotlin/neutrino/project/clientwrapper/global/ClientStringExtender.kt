package neutrino.project.clientwrapper.global

import neutrino.project.clientwrapper.request.executable.AsyncExecutableRequest
import okhttp3.Response
import java.io.File


fun String.sendGet(): Response {
    return GlobalClient.get(this)
}

fun String.sendGet(vararg body: Pair<String, String>): Response {
    return GlobalClient.get(
            url = this,
            body = body.toMap()
    )
}

fun String.sendGet(body: Map<String, String> = mapOf(), header: Map<String, String> = mapOf()): Response {
    return GlobalClient.get(
            url = this,
            body = body,
            header = header
    )
}

fun String.sendPost(body: Map<String, String>): Response {
    return GlobalClient.post(
            url = this,
            body = body
    )
}

fun String.sendPost(vararg body: Pair<String, String>): Response {
    return GlobalClient.post(
            url = this,
            body = body.toMap()
    )
}

fun String.sendPost(body: Map<String, String>, header: Map<String, String> = mapOf()): Response {
    return GlobalClient.get(
            url = this,
            body = body,
            header = header
    )
}

fun String.sendFile(body: Map<String, String> = mapOf(), header: Map<String, String> = mapOf(), name: String = "file", file: File): Response {
    return GlobalClient.sendFile(
            url = this,
            body = body,
            header = header,
            name = name,
            file = file
    )
}

fun String.asyncSendGet(body: Map<String, String> = mapOf(), header: Map<String, String> = mapOf()): AsyncExecutableRequest {
    return GlobalClient.asyncGet(
            url = this,
            body = body,
            header = header
    )
}

fun String.aasyncSendPost(body: Map<String, String>, header: Map<String, String> = mapOf()): AsyncExecutableRequest {
    return GlobalClient.asyncPost(
            url = this,
            body = body,
            header = header
    )
}

fun String.asyncSendFile(body: Map<String, String> = mapOf(), header: Map<String, String> = mapOf(), name: String = "file", file: File): AsyncExecutableRequest {
    return GlobalClient.asyncSendFile(
            url = this,
            body = body,
            header = header,
            name = name,
            file = file
    )
}