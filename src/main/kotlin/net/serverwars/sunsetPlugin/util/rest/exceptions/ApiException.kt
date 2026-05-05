package net.serverwars.sunsetPlugin.util.rest.exceptions

import io.ktor.http.HttpStatusCode

class ApiException(
    val status: HttpStatusCode,
    val errorCode: String,
    override val message: String,
) : Exception(message)
