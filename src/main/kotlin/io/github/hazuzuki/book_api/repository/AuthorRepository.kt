package io.github.hazuzuki.book_api.repository

import io.github.hazuzuki.book_api.domain.Author

interface AuthorRepository {
    fun save(author: Author): Author
    fun update(author: Author): Author
    fun findById(authorId: Int): Author?
    fun findAllByIds(ids: List<Int>): List<Author>
}