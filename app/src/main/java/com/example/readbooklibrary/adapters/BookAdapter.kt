package com.example.readbooklibrary.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readbooklibrary.R
import com.example.readbooklibrary.adapters.BookAdapter.BookViewHolder
import com.example.readbooklibrary.databinding.ItemBookBinding
import com.example.readbooklibrary.response.Book

class BookAdapter(
    private val onClick: (Book) -> Unit,
    private val onFavoriteClick: (Book) -> Unit
) : ListAdapter<Book, BookViewHolder>(Diff()) {

    class BookViewHolder(val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root)

    class Diff : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(old: Book, new: Book) = old.id == new.id
        override fun areContentsTheSame(old: Book, new: Book) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding =
            ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }


    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)

        holder.binding.apply {
            textTitle.text = book.title
            textAuthor.text = book.authors.firstOrNull()?.name ?: "Unknown"

            val imageUrl = book.formats["image/jpeg"]
            if (imageUrl != null) {
                Glide.with(root).load(imageUrl).into(imageBook)
            }

            btnFavorite.setImageResource(
                if (book.isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )

            btnFavorite.setOnClickListener {
                onFavoriteClick(book)
            }

            root.setOnClickListener { onClick(book) }
        }
    }

}
