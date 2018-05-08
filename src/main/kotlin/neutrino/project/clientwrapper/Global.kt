package neutrino.project.clientwrapper

import neutrino.project.clientwrapper.frame.*


object Global {
	var client: Client? = null
		get() {
			if (field == null)
				throw IllegalStateException("Global client is not set")

			return field
		}
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> post(client: Client, post: PostMethod<T>.() -> Unit): Expected<T> {
	val method = PostMethod<T>()
	post(method)
	return client.send(T::class, method)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> get(client: Client, get: GetMethod<T>.() -> Unit): Expected<T> {
	val method = GetMethod<T>()
	get(method)
	return client.send(T::class, method)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> jsonPost(client: Client, jsonPost: JsonPostMethod<T>.() -> Unit): Expected<T> {
	val method = JsonPostMethod<T>()
	jsonPost(method)
	return client.send(T::class, method)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> put(client: Client, put: PutMethod<T>.() -> Unit): Expected<T> {
	val method = PutMethod<T>()
	put(method)
	return client.send(T::class, method)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> delete(client: Client, delete: DeleteMethod<T>.() -> Unit): Expected<T> {
	val method = DeleteMethod<T>()
	delete(method)
	return client.send(T::class, method)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> jsonPut(client: Client, jsonPut: JsonPutMethod<T>.() -> Unit): Expected<T> {
	val method = JsonPutMethod<T>()
	jsonPut(method)
	return client.send(T::class, method)
}

inline fun <reified T : Any> post(post: PostMethod<T>.() -> Unit): Expected<T> = post(Global.client!!, post)

inline fun <reified T : Any> get(get: GetMethod<T>.() -> Unit): Expected<T> = get(Global.client!!, get)

inline fun <reified T : Any> jsonPost(jsonPost: JsonPostMethod<T>.() -> Unit): Expected<T> = jsonPost(Global.client!!, jsonPost)

inline fun <reified T : Any> put(put: PutMethod<T>.() -> Unit): Expected<T> = put(Global.client!!, put)

inline fun <reified T : Any> delete(delete: DeleteMethod<T>.() -> Unit): Expected<T> = delete(Global.client!!, delete)

inline fun <reified T : Any> jsonPut(jsonPut: JsonPutMethod<T>.() -> Unit): Expected<T> = jsonPut(Global.client!!, jsonPut)