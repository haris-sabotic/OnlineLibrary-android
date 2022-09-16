package com.ets.onlinebiblioteka.models.filters

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.ModelCardController
import com.ets.onlinebiblioteka.util.FilterModelController
import com.ets.onlinebiblioteka.util.GlobalData
import com.google.android.material.card.MaterialCardView

data class Zanr(
    val id: Int,

    val name: String,
    val photo: String,
    val description: String,
) : FilterModelController, ModelCardController<Zanr> {
    override fun getChipText(): String {
        return name
    }

    override fun getChipId(): Int {
        return id
    }

    override fun bind(
        context: Context,
        itemView: View,
        onCardClick: (item: Zanr) -> Unit,
        onAvailabilityIconClick: (available: Boolean) -> Unit
    ) {
        val textTitle = itemView.findViewById<TextView>(R.id.card_category_genre_text_title)
        val img = itemView.findViewById<ImageView>(R.id.card_category_genre_img)
        val card = itemView.findViewById<MaterialCardView>(R.id.card_category_genre_card)

        card.useCompatPadding = true
        textTitle.text = this.name

        Glide.with(context)
            .load(GlobalData.getImageUrl(this.photo))
            .centerCrop()
            .placeholder(R.drawable.placeholder_image)
            .into(img)

        card.setOnClickListener { onCardClick(this) }
    }
}
