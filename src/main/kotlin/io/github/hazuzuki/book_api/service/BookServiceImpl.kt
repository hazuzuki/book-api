package io.github.hazuzuki.book_api.service

import io.github.hazuzuki.book_api.domain.Book
import io.github.hazuzuki.book_api.domain.PublishStatus
import io.github.hazuzuki.book_api.dto.request.BookRequest
import io.github.hazuzuki.book_api.dto.response.BookWithAuthorsResponse
import io.github.hazuzuki.book_api.exception.NotFoundException
import io.github.hazuzuki.book_api.repository.BookRepository
import io.github.hazuzuki.book_api.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) : BookService {

    override fun createBook(request: BookRequest): BookWithAuthorsResponse {

        val authors = authorRepository.findAllByIds(request.authorIds)
        if (authors.size != request.authorIds.size) {
            throw NotFoundException("一部の著者が見つかりませんでした。")
        }

        val book = Book.create(
            id = 0, // 仮、実際はDB側で自動採番される前提
            title = request.title,
            price = request.price,
            publishStatus = PublishStatus.valueOf(request.publishStatus),
            authorIds = request.authorIds
        )

        val saved = bookRepository.save(book)
        return BookWithAuthorsResponse.from(saved, authors)
    }

    override fun updateBook(bookId: Int, request: BookRequest): BookWithAuthorsResponse {
        val existing = bookRepository.findById(bookId)
            ?: throw NotFoundException("指定されたIDの書籍が見つかりませんでした。: $bookId")

        val authors = authorRepository.findAllByIds(request.authorIds)
        if (authors.size != request.authorIds.size) {
            throw NotFoundException("一部の著者が見つかりませんでした。")
        }

        val updated = existing.changeAttributes(
            title = request.title,
            price = request.price,
            status = PublishStatus.valueOf(request.publishStatus),
            authorIds = request.authorIds
        )

        val saved = bookRepository.update(updated)
        return BookWithAuthorsResponse.from(saved, authors)
    }
}