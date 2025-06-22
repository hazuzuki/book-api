package io.github.hazuzuki.book_api.controller

import io.github.hazuzuki.book_api.dto.request.BookRequest
import io.github.hazuzuki.book_api.dto.response.BookWithAuthorsResponse
import io.github.hazuzuki.book_api.service.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService
) {

    @PostMapping
    fun create(@Valid @RequestBody request: BookRequest): ResponseEntity<BookWithAuthorsResponse> {
        val response = bookService.createBook(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @Valid @RequestBody request: BookRequest): ResponseEntity<BookWithAuthorsResponse> {
        val response = bookService.updateBook(id, request)
        return ResponseEntity.ok(response)
    }
}
