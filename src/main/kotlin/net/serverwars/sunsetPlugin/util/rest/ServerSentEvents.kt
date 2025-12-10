package net.serverwars.sunsetPlugin.util.rest

inline fun <reified T> parseSSEDto(json: String): T {
    val safeJson = json.replace("\"null\"", "null") // Why does this happen?
    return kotlinx.serialization.json.Json.decodeFromString(safeJson)
}