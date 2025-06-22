package io.github.hazuzuki.book_api.dto.response

import io.github.hazuzuki.book_api.domain.Author
import io.github.hazuzuki.book_api.domain.Book
import io.github.hazuzuki.book_api.domain.PublishStatus

data class BookWithAuthorsResponse(
    val id: Int,
    val title: String,
    val price: Int,
    val publishStatus: PublishStatus,
    val authors: List<AuthorResponse>
) {
    companion object {
        fun from(book: Book, authors: List<Author>): BookWithAuthorsResponse =
            BookWithAuthorsResponse(
                id = book.id,
                title = book.title,
                price = book.price,
                publishStatus = book.publishStatus,
                authors = authors.map { AuthorResponse.from(it) }
            )
    }
}