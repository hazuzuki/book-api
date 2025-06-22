package io.github.hazuzuki.book_api.repository

import io.github.hazuzuki.book_api.domain.Book
import io.github.hazuzuki.book_api.domain.PublishStatus
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import com.example.jooq.tables.Book.BOOK
import com.example.jooq.tables.BookAuthor.BOOK_AUTHOR

@Repository
@Transactional
class BookRepositoryImpl(
    private val dsl: DSLContext
) : BookRepository {

    override fun save(book: Book): Book {
        // insert book
        val bookId = dsl.insertInto(BOOK)
            .set(BOOK.TITLE, book.title)
            .set(BOOK.PRICE, book.price)
            .set(BOOK.PUBLISH_STATUS, book.publishStatus.name)
            .returningResult(BOOK.ID)
            .fetchOne()!!
            .value1()!!

        // insert book_author
        book.authorIds.forEach { authorId ->
            dsl.insertInto(BOOK_AUTHOR)
                .set(BOOK_AUTHOR.BOOK_ID, bookId)
                .set(BOOK_AUTHOR.AUTHOR_ID, authorId)
                .execute()
        }

        return book.copyWithId(bookId)
    }

    override fun update(book: Book): Book {
        // update book
        dsl.update(BOOK)
            .set(BOOK.TITLE, book.title)
            .set(BOOK.PRICE, book.price)
            .set(BOOK.PUBLISH_STATUS, book.publishStatus.name)
            .where(BOOK.ID.eq(book.id))
            .execute()

        // update book_author (全削除→再登録)
        dsl.deleteFrom(BOOK_AUTHOR)
            .where(BOOK_AUTHOR.BOOK_ID.eq(book.id))
            .execute()

        book.authorIds.forEach { authorId ->
            dsl.insertInto(BOOK_AUTHOR)
                .set(BOOK_AUTHOR.BOOK_ID, book.id)
                .set(BOOK_AUTHOR.AUTHOR_ID, authorId)
                .execute()
        }

        return book
    }

    override fun findById(id: Int): Book? {
        val record = dsl.selectFrom(BOOK)
            .where(BOOK.ID.eq(id))
            .fetchOne() ?: return null

        val authorIds = dsl.select(BOOK_AUTHOR.AUTHOR_ID)
            .from(BOOK_AUTHOR)
            .where(BOOK_AUTHOR.BOOK_ID.eq(id))
            .fetch()
            .map { it.value1() }

        return Book.create(
            id = record.id,
            title = record.title,
            price = record.price,
            publishStatus = PublishStatus.valueOf(record.publishStatus),
            authorIds = authorIds
        )
    }

    override fun findByAuthorId(authorId: Int): List<Book> {
        // JOIN book_author → book
        val records = dsl
            .select(BOOK.asterisk())
            .from(BOOK)
            .join(BOOK_AUTHOR).on(BOOK.ID.eq(BOOK_AUTHOR.BOOK_ID))
            .where(BOOK_AUTHOR.AUTHOR_ID.eq(authorId))
            .fetchInto(BOOK)

        val bookIds = records.mapNotNull { it.id }

        // 多対多のため、すべてのbookに対してauthorIdsを取得
        val authorMap = dsl
            .select(BOOK_AUTHOR.BOOK_ID, BOOK_AUTHOR.AUTHOR_ID)
            .from(BOOK_AUTHOR)
            .where(BOOK_AUTHOR.BOOK_ID.`in`(bookIds))
            .fetchGroups({ it.get(BOOK_AUTHOR.BOOK_ID)!! }) { it.get(BOOK_AUTHOR.AUTHOR_ID)!! }

        return records.map {
            Book.create(
                id = it.id,
                title = it.title,
                price = it.price,
                publishStatus = PublishStatus.valueOf(it.publishStatus),
                authorIds = authorMap[it.id] ?: emptyList()
            )
        }
    }
}