package net.serverwars.sunsetPlugin.util.rest.exceptions

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val errorCode: String,
    val message: String
)
