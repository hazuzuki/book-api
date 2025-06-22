package io.github.hazuzuki.book_api.domain

import java.time.LocalDate

data class Author(
    val id: Int,
    val name: String,
    val birthDate: LocalDate
)