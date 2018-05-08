# Http Client Framework
As core used OkHttpClient. Provided Kotlin DSL features. 
For better understand all of the futures, first read the [OkHttpClient documentation](https://github.com/square/okhttp/wiki).

Why if we have Retrofit?
We provided these features:
- Kotlin DSL;
- Pagination support;
- Classes as request model; 
- Iterable request support (as a collection of request model).

Example of Http Controller:
```kotlin
class MyAwesomeHttpController(client: Client) : AbstractHttpController(client) {

	fun myGet(id: String, headers: Params) = get<Response> {
		url = "/user/$id"
		headersParams = headers
	}

	fun myPost(headers: Map<String, String>, body: MyDataClass) = post<MyResponseEntity> {
		url = "/data"
		headersMap = headers
		bodyModel = body

		responseMapper = MyResponseMapper::class
	}

	fun myPaginatedGet() = get<List<Response>> {
		url = "/paginated"
		queriesCountable = Countable("page", 1, 1) { count, response ->
			response!!.closeAfter { response.code() == 404 }
		}
		
		responseMapper = EmptyResponseMapper::class
	}
	
	fun myIterablePost(iterableSearchBody: List<MyDataClass>) = post<List<MyResponseEntity>> {
		url = "/search"
		bodyModel = iterableSearchBody
		
		responseMapper = MyResponseMapper::class
	}
}
```