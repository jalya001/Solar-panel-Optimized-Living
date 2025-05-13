package no.SOL.data.weatherdata

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.IOException

class CustomHttpClient {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }) // PRODUCTION: remove all the irrelevant keys
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10_000  // 10 seconds timeout
            connectTimeoutMillis = 10_000  // 10 seconds connect timeout
            socketTimeoutMillis = 10_000   // 10 seconds socket timeout
        }
    }

    suspend inline fun <reified T> httpRequest(
        url: String,
        headers: Map<String, String> = emptyMap(),
        body: Any? = null,
        method: HttpMethod = HttpMethod.Get,
        maxRetries: Int = 3,
    ): Result<T?> {
        var attempt = 0
        var delayTime = 1000L

        while (attempt < maxRetries + 1) {
            try {
                val response: HttpResponse = client.request(url) {
                    this.method = method
                    headers.forEach { (key, value) -> header(key, value) }

                    if (body != null) {
                        contentType(ContentType.Application.Json)
                        setBody(body)
                    }
                }
                return when (response.status) {
                    HttpStatusCode.OK, HttpStatusCode.Created -> {
                        val data: T = response.body()
                        Result.success(data)
                    }

                    HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> {
                        Result.failure(ApiException(ApiError.AUTHORIZATION_ERROR))
                    }

                    HttpStatusCode.BadRequest -> {
                        Result.failure(ApiException(ApiError.REQUEST_ERROR))
                    }

                    HttpStatusCode.NotFound, HttpStatusCode.PreconditionFailed -> {
                        Result.success(null)
                    }

                    HttpStatusCode.TooManyRequests -> {
                        if (attempt >= maxRetries) return Result.failure(ApiException(ApiError.OVERLOAD_ERROR))
                        println("Rate limit exceeded, retrying in ${delayTime}ms...")
                        delay(delayTime)
                        delayTime *= 2
                        attempt++
                        continue
                    }

                    HttpStatusCode.InternalServerError, HttpStatusCode.BadGateway, HttpStatusCode.ServiceUnavailable -> {
                        Result.failure(ApiException(ApiError.SERVER_ERROR))
                    }

                    else -> {
                        Result.failure(ApiException(ApiError.UNKNOWN_ERROR))
                    }
                }
            } catch (e: HttpRequestTimeoutException) {
                if (attempt >= maxRetries) return Result.failure(ApiException(ApiError.TIMEOUT_ERROR))
                println("Timeout, retrying in ${delayTime}ms...")
            } catch (e: IOException) {
                if (attempt >= maxRetries) return Result.failure(ApiException(ApiError.NETWORK_ERROR))
                println("Network error: ${e.message}, retrying in ${delayTime}ms...")
            } catch (e: java.nio.channels.UnresolvedAddressException) {
                println("Network error: Host not found")
                return Result.failure(ApiException(ApiError.NETWORK_ERROR))
            } catch (e: Exception) {
                println(e)
                return Result.failure(ApiException(ApiError.UNKNOWN_ERROR))
            }

            delay(delayTime)
            delayTime *= 2
            attempt++
        }

        return Result.failure(ApiException(ApiError.UNKNOWN_ERROR))
    }
}

