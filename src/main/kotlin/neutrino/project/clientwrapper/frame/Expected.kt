package neutrino.project.clientwrapper.frame

import neutrino.project.clientwrapper.frame.processor.method.RequestMethodProcessor
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.function.Supplier


class Expected<T : Any>(private val methodProcessor: RequestMethodProcessor<T>) {

	/**
	 * Blocking send, used Call.execute()
	 *
	 * @see okhttp3.Call
	 */
	fun block(): T {
		return methodProcessor.process().getResult()
	}

	/**
	 * Async send, used Call.enqueue()
	 *
	 * @see okhttp3.Call
	 */
	fun async(): CompletableFuture<T> {
		return methodProcessor.processAsync()
				.thenApply(ResponseConsumer<T>::getResult)
	}

	/**
	 * Async send using blocking function in Client - Call.execute()
	 *
	 * @see okhttp3.Call
	 */
	fun fakeAsync(): CompletableFuture<T> {
		val executor = Executors.newSingleThreadExecutor()
		return CompletableFuture.supplyAsync(Supplier { block() }, executor)
	}
}