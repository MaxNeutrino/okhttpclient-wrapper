package neutrino.project.clientwrapper.frame

import neutrino.project.clientwrapper.Client

/**
 * You need to inherit this class to using kotlin dsl as future in your http controller class
 */
abstract class AbstractHttpController(protected val client: Client) {

	@Suppress("UNCHECKED_CAST")
	protected fun <T : Any> post(post: PostMethod<T>.() -> PostMethod<T>): T {
		return client.send(post as RequestMethod<T>)
	}

	@Suppress("UNCHECKED_CAST")
	protected fun <T : Any> get(get: GetMethod<T>.() -> GetMethod<T>): T {
		return client.send(get as RequestMethod<T>)
	}

	@Suppress("UNCHECKED_CAST")
	protected fun <T : Any> jsonPost(jsonPost: JsonPostMethod<T>.() -> JsonPostMethod<T>): T {
		return client.send(jsonPost as RequestMethod<T>)
	}
}