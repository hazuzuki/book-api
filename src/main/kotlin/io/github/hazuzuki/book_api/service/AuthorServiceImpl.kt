package io.github.hazuzuki.book_api.service

import io.github.hazuzuki.book_api.dto.request.AuthorRequest
import io.github.hazuzuki.book_api.dto.response.AuthorResponse
import io.github.hazuzuki.book_api.domain.Author
import io.github.hazuzuki.book_api.dto.response.AuthorBooksResponse
import io.github.hazuzuki.book_api.exception.NotFoundException
import io.github.hazuzuki.book_api.repository.AuthorRepository
import io.github.hazuzuki.book_api.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthorServiceImpl(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) : AuthorService {

    override fun createAuthor(request: AuthorRequest): AuthorResponse {
        val saved = authorRepository.save(
            Author(id = 0, name = request.name, birthDate = request.birthDate)
        )
        return AuthorResponse.from(saved)
    }

    override fun updateAuthor(id: Int, request: AuthorRequest): AuthorResponse {
        val existing = authorRepository.findById(id)
            ?: throw NotFoundException("指定されたIDの著者が見つかりませんでした。: $id")

        val updated = authorRepository.update(
            existing.copy(
                name = request.name,
                birthDate = request.birthDate
            )
        )
        return AuthorResponse.from(updated)
    }

    override fun getBooksByAuthorId(authorId: Int): AuthorBooksResponse {
        val author = authorRepository.findById(authorId)
            ?: throw NotFoundException("指定されたIDの著者が見つかりませんでした。: $authorId")

        val books = bookRepository.findByAuthorId(authorId)

        return AuthorBooksResponse.from(author, books)
    }
}