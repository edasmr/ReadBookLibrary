package com.example.readbooklibrary.api

import retrofit2.http.GET
import retrofit2.http.Url

interface TextApi {

    @GET
    suspend fun getBookText(@Url url: String): String
}
