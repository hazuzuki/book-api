package io.github.hazuzuki.book_api.dto.response

import io.github.hazuzuki.book_api.domain.Book
import io.github.hazuzuki.book_api.domain.PublishStatus

data class BookResponse(
    val id: Int,
    val title: String,
    val price: Int,
    val publishStatus: PublishStatus
) {
    companion object {
        fun from(book: Book): BookResponse =
            BookResponse(
                id = book.id,
                title = book.title,
                price = book.price,
                publishStatus = book.publishStatus
            )
    }
}