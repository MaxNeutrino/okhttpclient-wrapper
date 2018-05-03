package neutrino.project.clientwrapper.frame

import neutrino.project.clientwrapper.Client

/**
 * You need to inherit this class to using kotlin dsl as future in your http controller class
 */
abstract class AbstractHttpController(protected val client: Client) {

	@Suppress("UNCHECKED_CAST")
	protected fun <T : Any> post(post: PostMethod<T>.() -> Unit): T {
		val method = PostMethod<T>()
		post(method)
		return client.send(method)
	}

	@Suppress("UNCHECKED_CAST")
	protected fun <T : Any> get(get: GetMethod<T>.() -> Unit): T {
		val method = GetMethod<T>()
		get(method)
		return client.send(method)
	}

	@Suppress("UNCHECKED_CAST")
	protected fun <T : Any> jsonPost(jsonPost: JsonPostMethod<T>.() -> Unit): T {
		val method = JsonPostMethod<T>()
		jsonPost(method)
		return client.send(method)
	}
}