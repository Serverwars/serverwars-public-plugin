package net.serverwars.sunsetPlugin.util.rest.exceptions

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import net.serverwars.sunsetPlugin.Main

suspend inline fun <reified T> HttpResponse.parse(errorLogMessage: String): T {
    if (this.status.isSuccess()) {
        return runCatching {
            this.body<T>() // Attempt to parse successful response body
        }.getOrElse {
            // Given type does not match response body format
            Main.inst.logger.severe("[API] $status | $errorLogMessage | Given type does not match response body format.")

            throw ApiException(
                status = this.status,
                message = "Success status but could not parse response object.",
                errorCode = "UNEXPECTED_FORMAT"
            )
        }
    }

    val errorBody = runCatching {
        this.body<ApiErrorResponse>() // Attempt to parse error response body
    }.getOrElse {
        // Error response body does not match ApiErrorResponse format
        val rawBody = this.bodyAsText()
        Main.inst.logger.severe("[API] $status | $errorLogMessage | $rawBody")

        throw ApiException(
            status = this.status,
            message = "The server returned an unexpected error format.",
            errorCode = "UNKNOWN_ERROR"
        )
    }

    // Custom log message passed from the call site
    Main.inst.logger.info("[API] $status | $errorLogMessage | ${errorBody.message}")

    throw ApiException(
        status = this.status,
        message = errorBody.message,
        errorCode = errorBody.errorCode,
    )
}