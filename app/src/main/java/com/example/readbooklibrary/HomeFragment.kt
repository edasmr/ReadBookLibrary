package com.example.readbooklibrary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.readbooklibrary.adapters.BookAdapter
import com.example.readbooklibrary.database.AppDatabase
import com.example.readbooklibrary.databinding.FragmentHomeBinding
import com.example.readbooklibrary.response.Book
import com.example.readbooklibrary.response.BookEntity
import com.example.readbooklibrary.services.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: BookAdapter
    private var fullBookList = listOf<Book>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = BookAdapter(
            onClick = { book ->

                val textUrl =
                    book.formats["text/plain; charset=utf-8"]
                        ?: book.formats["text/plain"]

                if (textUrl == null) {
                    Toast.makeText(
                        requireContext(),
                        "This book has no text content.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@BookAdapter
                }
                val intent = Intent(requireContext(), BookReaderActivity::class.java)
                intent.putExtra("BOOK_URL", textUrl)
                startActivity(intent)
            },
            onFavoriteClick = { book ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = AppDatabase.getDatabase(requireContext()).bookDao()

                    dao.insert(
                        BookEntity(
                            id = book.id,
                            title = book.title,
                            author = book.authors.firstOrNull()?.name ?: "",
                            imageUrl = book.formats["image/jpeg"],
                            isFavorite = !book.isFavorite
                        )
                    )

                    val updatedList = fullBookList.map {
                        if (it.id == book.id) it.copy(isFavorite = !it.isFavorite)
                        else it
                    }

                    withContext(Dispatchers.Main) {
                        fullBookList = updatedList
                        adapter.submitList(updatedList)
                    }
                }
            }


        )

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.searchInput.addTextChangedListener { text ->
            val query = text.toString().trim().lowercase()

            val filtered = fullBookList.filter { book ->
                book.title.trim().lowercase().contains(query)
            }

            adapter.submitList(filtered)
        }
        loadBooks()

        return binding.root
    }

    private fun loadBooks() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getBooks()
                val dao = AppDatabase.getDatabase(requireContext()).bookDao()
                val favoriteIds = dao.getAllFavorites().map { it.id }.toSet()

                val newList = response.results.map { book ->
                    book.copy(isFavorite = favoriteIds.contains(book.id))
                }

                withContext(Dispatchers.Main) {
                    fullBookList = newList
                    syncFavorites()
                    adapter.submitList(newList)
                }

            } catch (e: Exception) {
                Log.e("HomeFragment", "API ERROR", e)
            }
        }
    }

    private suspend fun syncFavorites() {
        val dao = AppDatabase.getDatabase(requireContext()).bookDao()
        val favoriteIds = dao.getAllFavorites().map { it.id }.toSet()

        fullBookList.forEach {
            it.isFavorite = favoriteIds.contains(it.id)
        }
    }
}
