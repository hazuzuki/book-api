package io.github.hazuzuki.book_api.service

import io.github.hazuzuki.book_api.domain.Author
import io.github.hazuzuki.book_api.domain.Book
import io.github.hazuzuki.book_api.domain.PublishStatus
import io.github.hazuzuki.book_api.dto.request.BookRequest
import io.github.hazuzuki.book_api.exception.NotFoundException
import io.github.hazuzuki.book_api.repository.AuthorRepository
import io.github.hazuzuki.book_api.repository.BookRepository
import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import kotlin.test.assertFailsWith


class BookServiceImplTest {

    private lateinit var bookRepository: BookRepository
    private lateinit var authorRepository: AuthorRepository
    private lateinit var bookService: BookServiceImpl

    @BeforeEach
    fun setup() {
        bookRepository = mockk()
        authorRepository = mockk()
        bookService = BookServiceImpl(bookRepository, authorRepository)
    }

    @Test
    fun `should create book when all authors exist`() {
        // given
        val request = BookRequest(
            title = "Effective Kotlin",
            price = 3000,
            publishStatus = "UNPUBLISHED",
            authorIds = listOf(1, 2)
        )
        val authors = listOf(
            Author(1, "山田太郎", LocalDate.of(1980, 1, 1)),
            Author(2, "佐藤花子", LocalDate.of(1985, 5, 5))
        )
        val book = Book.create(
            id = 0,
            title = request.title,
            price = request.price,
            publishStatus = PublishStatus.UNPUBLISHED,
            authorIds = request.authorIds
        )
        val savedBook = book.copyWithId(100)

        every { authorRepository.findAllByIds(request.authorIds) } returns authors
        every { bookRepository.save(any()) } returns savedBook

        // when
        val response = bookService.createBook(request)

        // then
        assertEquals(savedBook.id, response.id)
        assertEquals(savedBook.title, response.title)
        assertEquals(2, response.authors.size)

        verify { authorRepository.findAllByIds(request.authorIds) }
        verify { bookRepository.save(match {
            it.title == request.title &&
                    it.price == request.price &&
                    it.authorIds == request.authorIds
        }) }
    }

    @Test
    fun `should throw when some authors not found on create`() {
        val request = BookRequest(
            title = "失敗するKotlin",
            price = 2500,
            publishStatus = "PUBLISHED",
            authorIds = listOf(1, 2)
        )
        val authors = listOf(Author(1, "存在する著者", LocalDate.of(1980, 1, 1)))

        every { authorRepository.findAllByIds(request.authorIds) } returns authors

        val ex = assertFailsWith<NotFoundException> {
            bookService.createBook(request)
        }

        assertEquals("一部の著者が見つかりませんでした。", ex.message)
    }

    @Test
    fun `should update book when book and authors exist`() {
        val bookId = 10
        val existing = Book.create(
            id = bookId,
            title = "旧タイトル",
            price = 1800,
            publishStatus = PublishStatus.UNPUBLISHED,
            authorIds = listOf(1)
        )
        val request = BookRequest(
            title = "新タイトル",
            price = 2000,
            publishStatus = "PUBLISHED",
            authorIds = listOf(1, 2)
        )
        val authors = listOf(
            Author(1, "著者A", LocalDate.of(1980, 1, 1)),
            Author(2, "著者B", LocalDate.of(1975, 12, 12))
        )
        val updated = existing.changeAttributes(
            title = request.title,
            price = request.price,
            status = PublishStatus.PUBLISHED,
            authorIds = request.authorIds
        )

        every { bookRepository.findById(bookId) } returns existing
        every { authorRepository.findAllByIds(request.authorIds) } returns authors
        every { bookRepository.update(any()) } returns updated

        val response = bookService.updateBook(bookId, request)

        assertEquals(request.title, response.title)
        assertEquals(request.price, response.price)
        assertEquals(2, response.authors.size)
        verify { bookRepository.findById(bookId) }
        verify { authorRepository.findAllByIds(request.authorIds) }
        verify { bookRepository.update(any()) }
    }

    @Test
    fun `should throw when book not found on update`() {
        val request = BookRequest(
            title = "更新失敗本",
            price = 1900,
            publishStatus = "UNPUBLISHED",
            authorIds = listOf(1)
        )
        every { bookRepository.findById(999) } returns null

        val ex = assertFailsWith<NotFoundException> {
            bookService.updateBook(999, request)
        }
        assertEquals("指定されたIDの書籍が見つかりませんでした。: 999", ex.message)
    }

    @Test
    fun `should throw when some authors not found on update`() {
        val bookId = 10
        val request = BookRequest(
            title = "新タイトル",
            price = 2000,
            publishStatus = "PUBLISHED",
            authorIds = listOf(1, 2)
        )
        val existing = Book.create(
            id = bookId,
            title = "旧タイトル",
            price = 1800,
            publishStatus = PublishStatus.UNPUBLISHED,
            authorIds = listOf(1)
        )

        every { bookRepository.findById(bookId) } returns existing
        every { authorRepository.findAllByIds(request.authorIds) } returns listOf(
            Author(1, "著者A", LocalDate.of(1980, 1, 1))
        )

        val ex = assertFailsWith<NotFoundException> {
            bookService.updateBook(bookId, request)
        }

        assertEquals("一部の著者が見つかりませんでした。", ex.message)
    }
}
