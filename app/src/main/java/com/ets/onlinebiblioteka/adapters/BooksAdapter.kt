package com.ets.onlinebiblioteka.adapters

import android.content.Context
import android.content.pm.ActivityInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.util.GlobalData
import com.google.android.material.card.MaterialCardView


class BooksAdapter(
    val items: MutableList<Book>,
    val context: Context,
    val onBookClick: (item: Book) -> Unit,
    val onAvailabilityIconClick: (available: Boolean) -> Unit,
    val wrapContentCardHeight: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textTitle = itemView.findViewById<TextView>(R.id.card_book_text_title)
        private var textAuthor = itemView.findViewById<TextView>(R.id.card_book_text_author)
        private var img = itemView.findViewById<ImageView>(R.id.card_book_img)
        private var iconAvailability = itemView.findViewById<ImageView>(R.id.card_book_icon_availability)
        private var card = itemView.findViewById<MaterialCardView>(R.id.card_book_card)

        fun bind(position: Int) {
            val item = items[position]

            textTitle.text = item.title

            // Only show first author(if it exists)
            if (item.authors.isNotEmpty()) {
                textAuthor.text = "by ${item.authors[0].name}"
            } else {
                textAuthor.text = ""
            }

            // set icon showing whether the book is available or not
            if (item.available) {
                iconAvailability.setImageResource(R.drawable.ic_book_available)
            } else {
                iconAvailability.setImageResource(R.drawable.ic_book_unavailable)
            }
            iconAvailability.setOnClickListener { onAvailabilityIconClick(item.available) }

            Glide.with(context)
                .load(GlobalData.getImageUrl(item.photo))
                .centerCrop()
                .placeholder(R.color.black)
                .into(img)

            card.setOnClickListener { onBookClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.card_book, parent, false)

        view.layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT

        if (wrapContentCardHeight) {
            view.layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addMoreBooks(books: List<Book>) {
        val pos = items.size
        items.addAll(books)
        notifyItemRangeChanged(pos, items.size)
    }
}