package neutrino.project.clientwrapper.frame.processor.method

import neutrino.project.clientwrapper.frame.ResponseConsumer
import java.util.concurrent.CompletableFuture


interface RequestMethodProcessor<T : Any> {
	fun process(): ResponseConsumer<T>
	fun processAsync(): CompletableFuture<ResponseConsumer<T>>
}