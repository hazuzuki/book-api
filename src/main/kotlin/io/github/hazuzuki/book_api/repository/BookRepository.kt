package io.github.hazuzuki.book_api.repository

import io.github.hazuzuki.book_api.domain.Book

interface BookRepository {
    fun save(book: Book): Book
    fun update(book: Book): Book
    fun findById(id: Int): Book?
    fun findByAuthorId(authorId: Int): List<Book>
}