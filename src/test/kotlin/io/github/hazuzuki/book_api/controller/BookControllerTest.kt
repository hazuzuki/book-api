package io.github.hazuzuki.book_api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.hazuzuki.book_api.domain.PublishStatus
import io.github.hazuzuki.book_api.dto.request.BookRequest
import io.github.hazuzuki.book_api.dto.response.AuthorResponse
import io.github.hazuzuki.book_api.dto.response.BookWithAuthorsResponse
import io.github.hazuzuki.book_api.exception.NotFoundException
import io.github.hazuzuki.book_api.service.BookService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

@WebMvcTest(BookController::class)
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var bookService: BookService

    @Test
    fun `create book - success`() {
        val request = BookRequest(
            title = "Test Book",
            price = 1500,
            publishStatus = "PUBLISHED",
            authorIds = listOf(1, 2)
        )

        val authors = listOf(
            AuthorResponse(1, "Author1", LocalDate.of(1980, 1, 1)),
            AuthorResponse(2, "Author2", LocalDate.of(1990, 2, 2))
        )
        val response = BookWithAuthorsResponse(
            id = 100,
            title = request.title,
            price = request.price,
            publishStatus = PublishStatus.PUBLISHED,
            authors = authors
        )

        `when`(bookService.createBook(request)).thenReturn(response)

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.title").value("Test Book"))
            .andExpect(jsonPath("$.price").value(1500))
            .andExpect(jsonPath("$.publishStatus").value("PUBLISHED"))
            .andExpect(jsonPath("$.authors[0].id").value(1))
            .andExpect(jsonPath("$.authors[1].name").value("Author2"))
    }

    @Test
    fun `create book - validation error (empty title)`() {
        val request = BookRequest(
            title = "",
            price = 1000,
            publishStatus = "PUBLISHED",
            authorIds = listOf(1)
        )

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.errors[0]").value("title: タイトルは必須です"))
    }

    @Test
    fun `create book - validation error (negative price)`() {
        val request = BookRequest(
            title = "Negative Price Book",
            price = -100,
            publishStatus = "PUBLISHED",
            authorIds = listOf(1)
        )

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors[0]").value("price: 価格は0以上である必要があります"))
    }

    @Test
    fun `create book - validation error (blank publishStatus)`() {
        val request = BookRequest(
            title = "Missing Status",
            price = 1200,
            publishStatus = "",
            authorIds = listOf(1)
        )

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors[0]").value("publishStatus: 出版ステータスは必須です"))
    }

    @Test
    fun `create book - validation error (empty authorIds)`() {
        val request = BookRequest(
            title = "No Author",
            price = 1200,
            publishStatus = "PUBLISHED",
            authorIds = listOf()
        )

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors[0]").value("authorIds: 著者IDは最低1人指定してください"))
    }

    @Test
    fun `create book - NotFoundException`() {
        val request = BookRequest(
            title = "Book",
            price = 1200,
            publishStatus = "PUBLISHED",
            authorIds = listOf(999)
        )

        `when`(bookService.createBook(request)).thenThrow(NotFoundException("一部の著者が見つかりませんでした。"))

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("一部の著者が見つかりませんでした。"))
    }
}