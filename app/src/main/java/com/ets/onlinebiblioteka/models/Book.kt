package com.ets.onlinebiblioteka.models

import android.content.Context
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.util.GlobalData
import com.google.android.material.card.MaterialCardView
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    var id: Int,
    var title: String,
    var authors: ArrayList<String>,
    var photo: String,
    var available: Boolean
) : Parcelable, ModelCardController<Book> {
    override fun bind(
        context: Context,
        itemView: View,
        onCardClick: (item: Book) -> Unit,
        onAvailabilityIconClick: (available: Boolean) -> Unit
    ) {
        val textTitle = itemView.findViewById<TextView>(R.id.card_book_text_title)
        val textAuthor = itemView.findViewById<TextView>(R.id.card_book_text_author)
        val img = itemView.findViewById<ImageView>(R.id.card_book_img)
        val iconAvailability = itemView.findViewById<ImageView>(R.id.card_book_icon_availability)
        val card = itemView.findViewById<MaterialCardView>(R.id.card_book_card)

        card.useCompatPadding = true
        textTitle.text = this.title

        if (this.authors.isNotEmpty()) {
            textAuthor.text = "by ${this.authors[0]}"
        } else {
            textAuthor.text = ""
        }

        if (this.available) {
            iconAvailability.setImageResource(R.drawable.ic_book_available)
        } else {
            iconAvailability.setImageResource(R.drawable.ic_book_unavailable)
        }
        iconAvailability.setOnClickListener { onAvailabilityIconClick(this.available) }

        Glide.with(context)
            .load(GlobalData.getImageUrl(this.photo))
            .centerCrop()
            .placeholder(R.color.black)
            .into(img)

        card.setOnClickListener { onCardClick(this) }
    }
}
