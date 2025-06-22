package io.github.hazuzuki.book_api.service

import io.github.hazuzuki.book_api.dto.request.AuthorRequest
import io.github.hazuzuki.book_api.dto.response.AuthorBooksResponse
import io.github.hazuzuki.book_api.dto.response.AuthorResponse

interface AuthorService {
    fun createAuthor(request: AuthorRequest): AuthorResponse
    fun updateAuthor(id: Int, request: AuthorRequest): AuthorResponse
    fun getBooksByAuthorId(authorId: Int): AuthorBooksResponse
}