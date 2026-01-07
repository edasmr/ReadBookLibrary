package com.example.readbooklibrary.response

data class BookResponse(
    val results: List<Book>
)

data class Book(
    val id: Int,
    val title: String,
    val authors: List<Author>,
    val formats: Map<String, String>,
    var isFavorite: Boolean = false
)

data class Author(
    val name: String
)