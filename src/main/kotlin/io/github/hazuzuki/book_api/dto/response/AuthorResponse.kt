package io.github.hazuzuki.book_api.dto.response

import io.github.hazuzuki.book_api.domain.Author
import java.time.LocalDate

data class AuthorResponse(
    val id: Int,
    val name: String,
    val birthDate: LocalDate
) {
    companion object {
        fun from(domain: Author): AuthorResponse =
            AuthorResponse(
                id = domain.id,
                name = domain.name,
                birthDate = domain.birthDate
            )
    }
}