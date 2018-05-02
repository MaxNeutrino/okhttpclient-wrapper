package neutrino.project.clientwrapper.util.ext

import java.util.concurrent.CompletableFuture


fun <T> List<CompletableFuture<T>>.collect(): CompletableFuture<List<T>> {
	val allFutures = CompletableFuture.allOf(*this.toTypedArray())
	return allFutures.thenApply {
		this.map(CompletableFuture<T>::join)
	}
}