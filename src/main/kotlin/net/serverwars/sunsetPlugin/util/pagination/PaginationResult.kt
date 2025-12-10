package net.serverwars.sunsetPlugin.util.pagination

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResult(
    val pageNumber: Int? = null,
    val pageSize: Int? = null,
    val totalNumberOfElements: Long? = null,
    val totalNumberOfPages: Long? = null,
)