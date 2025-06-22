package io.github.hazuzuki.book_api.domain

class Book private constructor(
    val id: Int,
    val title: String,
    val price: Int,
    val publishStatus: PublishStatus,
    val authorIds: List<Int>
) {
    fun changeStatus(newStatus: PublishStatus): Book {
        if (this.publishStatus == PublishStatus.PUBLISHED && newStatus == PublishStatus.UNPUBLISHED) {
            throw IllegalStateException("出版済みから未出版には変更できません")
        }
        return Book(id, title, price, newStatus, authorIds)
    }

    fun changeAttributes(
        title: String = this.title,
        price: Int = this.price,
        status: PublishStatus = this.publishStatus,
        authorIds: List<Int> = this.authorIds
    ): Book {
        validateInvariant(price, authorIds)
        val updated = Book(this.id, title, price, this.publishStatus, authorIds)
        return updated.changeStatus(status)
    }

    fun copyWithId(newId: Int): Book =
        Book(newId, title, price, publishStatus, authorIds)

    companion object {
        fun create(
            id: Int,
            title: String,
            price: Int,
            publishStatus: PublishStatus,
            authorIds: List<Int>
        ): Book {
            validateInvariant(price, authorIds)
            return Book(id, title, price, publishStatus, authorIds)
        }

        private fun validateInvariant(price: Int, authorIds: List<Int>) {
            require(price >= 0) { "価格は0以上である必要があります" }
            require(authorIds.isNotEmpty()) { "著者は1人以上必要です" }
        }
    }
}

enum class PublishStatus {
    UNPUBLISHED, PUBLISHED
}