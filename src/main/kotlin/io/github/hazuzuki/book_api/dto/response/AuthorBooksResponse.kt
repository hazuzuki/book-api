package io.github.hazuzuki.book_api.dto.response

import io.github.hazuzuki.book_api.domain.Author
import io.github.hazuzuki.book_api.domain.Book

data class AuthorBooksResponse(
    val authorId: Int,
    val authorName: String,
    val books: List<BookResponse>
) {
    companion object {
        fun from(author: Author, books: List<Book>): AuthorBooksResponse =
            AuthorBooksResponse(
                authorId = author.id,
                authorName = author.name,
                books = books.map { BookResponse.from(it) }
            )
    }
}