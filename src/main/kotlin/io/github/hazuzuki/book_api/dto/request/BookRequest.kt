package io.github.hazuzuki.book_api.dto.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class BookRequest (
    @field:NotBlank(message = "タイトルは必須です")
    val title: String,

    @field:Min(value = 0, message = "価格は0以上である必要があります")
    val price: Int,

    @field:NotBlank(message = "出版ステータスは必須です")
    val publishStatus: String,

    @field:NotEmpty(message = "著者IDは最低1人指定してください")
    val authorIds: List<Int>
)