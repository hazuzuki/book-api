package io.github.hazuzuki.book_api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.hazuzuki.book_api.domain.PublishStatus
import io.github.hazuzuki.book_api.dto.request.AuthorRequest
import io.github.hazuzuki.book_api.dto.response.AuthorBooksResponse
import io.github.hazuzuki.book_api.dto.response.AuthorResponse
import io.github.hazuzuki.book_api.dto.response.BookResponse
import io.github.hazuzuki.book_api.exception.NotFoundException
import io.github.hazuzuki.book_api.service.AuthorService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

@WebMvcTest(AuthorController::class)
class AuthorControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var authorService: AuthorService

    @Test
    fun `create author - success`() {
        val request = AuthorRequest("Test Author", LocalDate.of(1980, 1, 1))
        val response = AuthorResponse(1, request.name, request.birthDate)

        `when`(authorService.createAuthor(request)).thenReturn(response)

        mockMvc.perform(
            post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Author"))
            .andExpect(jsonPath("$.birthDate").value("1980-01-01"))

        verify(authorService).createAuthor(request)
    }

    @Test
    fun `create author - validation error (blank name)`() {
        val request = AuthorRequest("", LocalDate.of(1980, 1, 1))

        mockMvc.perform(
            post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors[0]").value("name: 著者名は必須です"))
    }

    @Test
    fun `create author - validation error (future birthDate)`() {
        val request = AuthorRequest("Test", LocalDate.now().plusDays(1))

        mockMvc.perform(
            post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors[0]").value("birthDate: 生年月日は現在より過去の日付である必要があります"))
    }

    @Test
    fun `update author - success`() {
        val request = AuthorRequest("Updated", LocalDate.of(1975, 5, 5))
        val response = AuthorResponse(2, request.name, request.birthDate)

        `when`(authorService.updateAuthor(2, request)).thenReturn(response)

        mockMvc.perform(
            put("/api/authors/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Updated"))
            .andExpect(jsonPath("$.birthDate").value("1975-05-05"))

        verify(authorService).updateAuthor(2, request)
    }

    @Test
    fun `update author - not found`() {
        val request = AuthorRequest("Name", LocalDate.of(1990, 1, 1))

        `when`(authorService.updateAuthor(999, request))
            .thenThrow(NotFoundException("指定されたIDの著者が見つかりませんでした。: 999"))

        mockMvc.perform(
            put("/api/authors/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("指定されたIDの著者が見つかりませんでした。: 999"))
    }

    @Test
    fun `get books by authorId - success`() {
        val response = AuthorBooksResponse(
            authorId = 1,
            authorName = "Test Author",
            books = listOf(
                BookResponse(1, "Book A", 1200, PublishStatus.PUBLISHED),
                BookResponse(2, "Book B", 1000, PublishStatus.UNPUBLISHED)
            )
        )

        `when`(authorService.getBooksByAuthorId(1)).thenReturn(response)

        mockMvc.perform(get("/api/authors/1/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.authorId").value(1))
            .andExpect(jsonPath("$.authorName").value("Test Author"))
            .andExpect(jsonPath("$.books").isArray)
            .andExpect(jsonPath("$.books[0].title").value("Book A"))
    }

    @Test
    fun `get books by authorId - not found`() {
        `when`(authorService.getBooksByAuthorId(404))
            .thenThrow(NotFoundException("指定されたIDの著者が見つかりませんでした。: 404"))

        mockMvc.perform(get("/api/authors/404/books"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("指定されたIDの著者が見つかりませんでした。: 404"))
    }
}