package neutrino.project.clientwrapper

import io.javalin.ApiBuilder.*
import io.javalin.Javalin
import neutrino.project.clientwrapper.data.COUNT_POST_PARAM_NAME
import neutrino.project.clientwrapper.data.CountableTestData


class MockWebServer {

	private val app = Javalin.create().apply {
		port(7000)
		exception(Exception::class.java) { e, _ -> e.printStackTrace() }
		error(404) { ctx -> ctx.json("not found") }
	}

	fun start() {
		app.start()

		app.routes {
			path("main") {
				get("/:param") { ctx ->
					val param = ctx.param("param")

					if(param!!.toInt() > 6 ) {
						ctx.status(404)
						ctx.result("error")
					} else {
						ctx.status(200)
						ctx.result(param)
					}
				}

				post("/") { ctx ->
					val body = ctx.body()
					val responseBody = body

					ctx.cookieStore("hello", "world")

					ctx.status(200)
					ctx.result(responseBody)
				}

				post("/count") { ctx ->
					val params = ctx.body().split("&").map {
						val vals = it.split("=")
						vals.first() to vals.last()
					}.toMap()
					val count = params[COUNT_POST_PARAM_NAME]!!.toInt()

					if(count > 2) {
						ctx.status(404)
						ctx.result("error")
					} else {

						val responseBody = params.toList().joinToString(", ") { "${it.first}=${it.second}" }

						ctx.cookieStore("hello", "world")

						ctx.status(200)
						ctx.result(responseBody)
					}
				}
				post("/count/json/:param") { ctx ->
					val count = ctx.param("param")!!.toInt()

					val params = ctx.body()

					if(count > 2) {
						ctx.status(404)
						ctx.result("error")
					} else {

						val responseBody = "{ count : $count, params : $params }"

						ctx.cookieStore("hello", "world")

						ctx.status(200)
						ctx.result(responseBody)
					}
				}
			}
		}
	}

	fun stop() {
		app.stop()
	}
}