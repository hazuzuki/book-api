package io.github.hazuzuki.book_api.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val errorCode: String,
    val message: String,
    val timestamp: LocalDateTime,
    val path: String,
    val errors: List<String>? = null
)
