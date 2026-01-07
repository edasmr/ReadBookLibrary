package com.example.readbooklibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.readbooklibrary.adapters.BookAdapter
import com.example.readbooklibrary.database.AppDatabase
import com.example.readbooklibrary.databinding.FragmentFavoriteBinding
import com.example.readbooklibrary.response.Author
import com.example.readbooklibrary.response.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        adapter = BookAdapter(
            onClick = { book ->
                val action =
                    FavoriteFragmentDirections.actionFavoriteToBookReader(book.id.toString())
                findNavController().navigate(action)
            },
            onFavoriteClick = { book ->
                removeFavorite(book)
            }
        )

        binding.recyclerViewFavorites.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = adapter

        loadFavorites()

        return binding.root
    }

    private fun loadFavorites() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(requireContext()).bookDao()
            val favorites = dao.getAllFavorites()

            val books = favorites.map {
                Book(
                    id = it.id,
                    title = it.title,
                    authors = listOf(Author(it.author)),
                    formats = mapOf("image/jpeg" to it.imageUrl.orEmpty()),
                    isFavorite = true
                )
            }

            withContext(Dispatchers.Main) {
                adapter.submitList(books)
            }
        }
    }

    private fun removeFavorite(book: Book) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(requireContext()).bookDao()
            dao.deleteFavorite(book.id)

            withContext(Dispatchers.Main) {
                loadFavorites()
            }
        }
    }
}



