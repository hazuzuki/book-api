package io.github.hazuzuki.book_api.service

import io.github.hazuzuki.book_api.domain.Author
import io.github.hazuzuki.book_api.domain.Book
import io.github.hazuzuki.book_api.domain.PublishStatus
import io.github.hazuzuki.book_api.dto.request.AuthorRequest
import io.github.hazuzuki.book_api.exception.NotFoundException
import io.github.hazuzuki.book_api.repository.AuthorRepository
import io.github.hazuzuki.book_api.repository.BookRepository
import io.mockk.*
import org.junit.jupiter.api.*
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthorServiceImplTest {

    private lateinit var authorRepository: AuthorRepository
    private lateinit var bookRepository: BookRepository
    private lateinit var authorService: AuthorServiceImpl

    @BeforeEach
    fun setUp() {
        authorRepository = mockk()
        bookRepository = mockk()
        authorService = AuthorServiceImpl(authorRepository, bookRepository)
    }

    @Test
    fun `should create author successfully`() {
        val request = AuthorRequest("Alice", LocalDate.of(1980, 1, 1))
        val saved = Author(1, "Alice", request.birthDate)

        every { authorRepository.save(any()) } returns saved

        val result = authorService.createAuthor(request)

        assertEquals(saved.id, result.id)
        assertEquals(saved.name, result.name)
        assertEquals(saved.birthDate, result.birthDate)

        verify { authorRepository.save(match { it.name == request.name && it.birthDate == request.birthDate }) }
    }

    @Test
    fun `should update author successfully when found`() {
        val request = AuthorRequest("Bob", LocalDate.of(1990, 5, 5))
        val existing = Author(10, "Old Bob", LocalDate.of(1985, 1, 1))
        val updated = existing.copy(name = request.name, birthDate = request.birthDate)

        every { authorRepository.findById(10) } returns existing
        every { authorRepository.update(updated) } returns updated

        val result = authorService.updateAuthor(10, request)

        assertEquals(10, result.id)
        assertEquals("Bob", result.name)
        assertEquals(LocalDate.of(1990, 5, 5), result.birthDate)

        verify { authorRepository.findById(10) }
        verify { authorRepository.update(updated) }
    }

    @Test
    fun `should throw NotFoundException when updating non-existent author`() {
        val request = AuthorRequest("Unknown", LocalDate.of(2000, 1, 1))
        every { authorRepository.findById(99) } returns null

        val exception = assertFailsWith<NotFoundException> {
            authorService.updateAuthor(99, request)
        }

        assertEquals("指定されたIDの著者が見つかりませんでした。: 99", exception.message)
        verify { authorRepository.findById(99) }
    }

    @Test
    fun `should return books by authorId when author exists`() {
        val author = Author(5, "AuthorX", LocalDate.of(1970, 1, 1))
        val books = listOf(
            Book.create(1, "BookA", 1000, PublishStatus.PUBLISHED, listOf(5)),
            Book.create(2, "BookB", 1500, PublishStatus.UNPUBLISHED, listOf(5))
        )

        every { authorRepository.findById(5) } returns author
        every { bookRepository.findByAuthorId(5) } returns books

        val result = authorService.getBooksByAuthorId(5)

        assertEquals(author.id, result.authorId)
        assertEquals(author.name, result.authorName)
        assertEquals(2, result.books.size)
        assertEquals("BookA", result.books[0].title)

        verify { authorRepository.findById(5) }
        verify { bookRepository.findByAuthorId(5) }
    }

    @Test
    fun `should throw NotFoundException when author not found in getBooksByAuthorId`() {
        every { authorRepository.findById(100) } returns null

        val exception = assertFailsWith<NotFoundException> {
            authorService.getBooksByAuthorId(100)
        }

        assertEquals("指定されたIDの著者が見つかりませんでした。: 100", exception.message)
        verify { authorRepository.findById(100) }
    }
}