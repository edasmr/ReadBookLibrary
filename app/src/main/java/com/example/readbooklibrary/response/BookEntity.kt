package com.example.readbooklibrary.response
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val author: String,
    val imageUrl: String?,
    val isFavorite: Boolean
)
