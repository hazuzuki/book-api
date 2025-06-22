package io.github.hazuzuki.book_api.service


import io.github.hazuzuki.book_api.dto.request.BookRequest
import io.github.hazuzuki.book_api.dto.response.BookWithAuthorsResponse

interface BookService {
    fun createBook(request: BookRequest): BookWithAuthorsResponse
    fun updateBook(bookId: Int, request: BookRequest): BookWithAuthorsResponse
}