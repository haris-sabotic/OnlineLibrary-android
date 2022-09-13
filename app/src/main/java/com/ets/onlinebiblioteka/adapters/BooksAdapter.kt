package com.ets.onlinebiblioteka.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.util.GlobalData


class BooksAdapter(
    val items: List<Book>,
    val context: Context,
    val onBookClick: (itemIndex: Int) -> Unit,
    val onAvailabilityIconClick: (available: Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textTitle = itemView.findViewById<TextView>(R.id.card_book_text_title)
        private var textAuthor = itemView.findViewById<TextView>(R.id.card_book_text_author)
        private var img = itemView.findViewById<ImageView>(R.id.card_book_img)
        private var iconAvailability = itemView.findViewById<ImageView>(R.id.card_book_icon_availability)


        fun bind(position: Int) {
            val item = items[position]

            textTitle.text = item.title

            if (item.authors.isNotEmpty()) {
                textAuthor.text = "by ${item.authors[0]}"
            } else {
                textAuthor.text = ""
            }

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

            itemView.setOnClickListener { onBookClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.card_book, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}