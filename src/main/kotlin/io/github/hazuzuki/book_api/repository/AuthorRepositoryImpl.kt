package io.github.hazuzuki.book_api.repository

import io.github.hazuzuki.book_api.domain.Author
import com.example.jooq.tables.Author.AUTHOR
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorRepositoryImpl(
    private val dsl: DSLContext
) : AuthorRepository {

    override fun save(author: Author): Author {
        val id = dsl.insertInto(AUTHOR)
            .set(AUTHOR.NAME, author.name)
            .set(AUTHOR.BIRTH_DATE, author.birthDate)
            .returningResult(AUTHOR.ID)
            .fetchOne()!!
            .value1()!!

        return author.copy(id = id)
    }

    override fun update(author: Author): Author {
        dsl.update(AUTHOR)
            .set(AUTHOR.NAME, author.name)
            .set(AUTHOR.BIRTH_DATE, author.birthDate)
            .where(AUTHOR.ID.eq(author.id))
            .execute()

        return author
    }

    override fun findById(authorId: Int): Author? {
        return dsl.selectFrom(AUTHOR)
            .where(AUTHOR.ID.eq(authorId))
            .fetchOne()
            ?.let {
                Author(
                    id = it.id!!,
                    name = it.name!!,
                    birthDate = it.birthDate!!
                )
            }
    }

    override fun findAllByIds(ids: List<Int>): List<Author> {
        if (ids.isEmpty()) return emptyList()

        return dsl.selectFrom(AUTHOR)
            .where(AUTHOR.ID.`in`(ids))
            .fetch()
            .map {
                Author(
                    id = it.id!!,
                    name = it.name!!,
                    birthDate = it.birthDate!!
                )
            }
    }
}
