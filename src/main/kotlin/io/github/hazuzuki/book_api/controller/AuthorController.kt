package io.github.hazuzuki.book_api.controller

import io.github.hazuzuki.book_api.dto.request.AuthorRequest
import io.github.hazuzuki.book_api.dto.response.AuthorBooksResponse
import io.github.hazuzuki.book_api.dto.response.AuthorResponse
import io.github.hazuzuki.book_api.service.AuthorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/authors")
class AuthorController(
    private val authorService: AuthorService
) {

    @PostMapping
    fun create(@Valid @RequestBody request: AuthorRequest): ResponseEntity<AuthorResponse> {
        val response = authorService.createAuthor(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @Valid @RequestBody request: AuthorRequest): ResponseEntity<AuthorResponse> {
        val response = authorService.updateAuthor(id, request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/books")
    fun getBooksByAuthorId(@PathVariable id: Int): ResponseEntity<AuthorBooksResponse> {
        val response = authorService.getBooksByAuthorId(id)
        return ResponseEntity.ok(response)
    }
}