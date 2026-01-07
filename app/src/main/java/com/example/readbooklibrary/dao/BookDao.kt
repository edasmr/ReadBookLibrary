package com.example.readbooklibrary.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.readbooklibrary.response.BookEntity


@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity)

    @Query("SELECT * FROM books WHERE isFavorite = 1")
    suspend fun getAllFavorites(): List<BookEntity>

    @Query("UPDATE books SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteFavorite(id: Int)


}
