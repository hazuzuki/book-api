package io.github.hazuzuki.book_api.dto.request

import java.time.LocalDate
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past

data class AuthorRequest(
    @field:NotBlank(message = "著者名は必須です")
    val name: String,

    @field:Past(message = "生年月日は現在より過去の日付である必要があります")
    val birthDate: LocalDate
)