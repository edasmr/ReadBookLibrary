package com.example.readbooklibrary.api

import com.example.readbooklibrary.response.Book
import com.example.readbooklibrary.response.BookResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("books")
    suspend fun getBooks(): BookResponse

    @GET("books/{id}")
    suspend fun getBookDetail(@Path("id") id: Int): Book
}