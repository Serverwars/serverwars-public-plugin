package net.serverwars.sunsetPlugin.util.rest.exceptions

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.runAsync

data class ErrorCodeMessage(
    val key: String,
    val args: List<String> = emptyList(),
)

fun callApi(
    audience: Audience = Audience.empty(),
    errorCodeMap: Map<String, ErrorCodeMessage> = emptyMap(),
    fallbackErrorKey: String = "command.error.api_exception",
    block: suspend () -> Unit
) = runAsync {
    try {
        block()
    } catch (error: ApiException) {
        val errorCodeMessage = errorCodeMap[error.errorCode]
        if (errorCodeMessage == null) {
            audience.sendTranslatedMessage(fallbackErrorKey)
        } else {
            audience.sendTranslatedMessage(errorCodeMessage.key, errorCodeMessage.args)
        }
    } catch (e: Exception) { // Handle non-API exceptions (timeouts, null pointers, etc.)
        Main.inst.logger.severe("Unexpected error: ${e.message}")
        audience.sendTranslatedMessage(fallbackErrorKey)
    }
}
