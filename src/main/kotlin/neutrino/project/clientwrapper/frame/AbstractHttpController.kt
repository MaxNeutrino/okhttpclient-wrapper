package neutrino.project.clientwrapper.frame

import neutrino.project.clientwrapper.Client

/**
 * You need to inherit this class to using kotlin dsl as future in your http controller class
 */
abstract class AbstractHttpController(protected val client: Client) {

	@Suppress("UNCHECKED_CAST")
	protected inline fun <reified T : Any> post(post: PostMethod<T>.() -> Unit): Expected<T> {
		val method = PostMethod<T>()
		post(method)
		return client.send(T::class, method)
	}

	@Suppress("UNCHECKED_CAST")
	protected inline fun <reified T : Any> get(get: GetMethod<T>.() -> Unit): Expected<T> {
		val method = GetMethod<T>()
		get(method)
		return client.send(T::class, method)
	}

	@Suppress("UNCHECKED_CAST")
	protected inline fun <reified T : Any> jsonPost(jsonPost: JsonPostMethod<T>.() -> Unit): Expected<T> {
		val method = JsonPostMethod<T>()
		jsonPost(method)
		return client.send(T::class, method)
	}

	@Suppress("UNCHECKED_CAST")
	protected inline fun <reified T : Any> put(put: PutMethod<T>.() -> Unit): Expected<T> {
		val method = PutMethod<T>()
		put(method)
		return client.send(T::class, method)
	}

	@Suppress("UNCHECKED_CAST")
	protected inline fun <reified T : Any> delete(delete: DeleteMethod<T>.() -> Unit): Expected<T> {
		val method = DeleteMethod<T>()
		delete(method)
		return client.send(T::class, method)
	}

	@Suppress("UNCHECKED_CAST")
	protected inline fun <reified T : Any> jsonPut(jsonPut: JsonPutMethod<T>.() -> Unit): Expected<T> {
		val method = JsonPutMethod<T>()
		jsonPut(method)
		return client.send(T::class, method)
	}
}