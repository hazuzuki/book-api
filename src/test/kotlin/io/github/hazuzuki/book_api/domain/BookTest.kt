package io.github.hazuzuki.book_api.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BookTest {

    @Test
    fun `create book - valid input`() {
        val book = Book.create(
            id = 1,
            title = "Effective Kotlin",
            price = 3000,
            publishStatus = PublishStatus.UNPUBLISHED,
            authorIds = listOf(1, 2)
        )

        assertEquals("Effective Kotlin", book.title)
        assertEquals(3000, book.price)
        assertEquals(PublishStatus.UNPUBLISHED, book.publishStatus)
        assertEquals(listOf(1, 2), book.authorIds)
    }

    @Test
    fun `create book - negative price throws exception`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Book.create(1, "Title", -500, PublishStatus.UNPUBLISHED, listOf(1))
        }
        assertEquals("価格は0以上である必要があります", exception.message)
    }

    @Test
    fun `create book - empty author list throws exception`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Book.create(1, "Title", 1000, PublishStatus.UNPUBLISHED, emptyList())
        }
        assertEquals("著者は1人以上必要です", exception.message)
    }

    @Test
    fun `changeStatus from UNPUBLISHED to PUBLISHED works`() {
        val book = Book.create(1, "Test", 1000, PublishStatus.UNPUBLISHED, listOf(1))
        val updated = book.changeStatus(PublishStatus.PUBLISHED)
        assertEquals(PublishStatus.PUBLISHED, updated.publishStatus)
    }

    @Test
    fun `changeStatus from PUBLISHED to UNPUBLISHED throws exception`() {
        val book = Book.create(1, "Test", 1000, PublishStatus.PUBLISHED, listOf(1))
        val exception = assertThrows(IllegalStateException::class.java) {
            book.changeStatus(PublishStatus.UNPUBLISHED)
        }
        assertEquals("出版済みから未出版には変更できません", exception.message)
    }

    @Test
    fun `changeAttributes updates fields and keeps invariants`() {
        val book = Book.create(1, "Old Title", 1000, PublishStatus.UNPUBLISHED, listOf(1))
        val updated = book.changeAttributes(
            title = "New Title",
            price = 2000,
            status = PublishStatus.PUBLISHED,
            authorIds = listOf(1, 2)
        )
        assertEquals("New Title", updated.title)
        assertEquals(2000, updated.price)
        assertEquals(PublishStatus.PUBLISHED, updated.publishStatus)
        assertEquals(listOf(1, 2), updated.authorIds)
    }

    @Test
    fun `copyWithId changes only id`() {
        val book = Book.create(0, "Test", 1000, PublishStatus.UNPUBLISHED, listOf(1))
        val copied = book.copyWithId(123)
        assertEquals(123, copied.id)
        assertEquals(book.title, copied.title)
        assertEquals(book.publishStatus, copied.publishStatus)
    }
}