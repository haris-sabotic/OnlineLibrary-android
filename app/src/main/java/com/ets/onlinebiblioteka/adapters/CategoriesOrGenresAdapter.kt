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
import com.ets.onlinebiblioteka.models.CategoryOrGenre
import com.ets.onlinebiblioteka.util.GlobalData
import com.google.android.material.card.MaterialCardView


class CategoriesOrGenresAdapter(
    val items: MutableList<CategoryOrGenre>,
    val context: Context,
    val onCardClick: (item: CategoryOrGenre) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textTitle = itemView.findViewById<TextView>(R.id.card_category_genre_text_title)
        private var img = itemView.findViewById<ImageView>(R.id.card_category_genre_img)
        private var card = itemView.findViewById<MaterialCardView>(R.id.card_category_genre_card)

        fun bind(position: Int) {
            val item = items[position]

            textTitle.text = item.name

            Glide.with(context)
                .load(GlobalData.getImageUrl(item.photo))
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(img)

            card.setOnClickListener { onCardClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.card_category_genre, parent, false)

        view.layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addMoreItems(data: List<CategoryOrGenre>) {
        val pos = items.size
        items.addAll(data)
        notifyItemRangeChanged(pos, items.size)
    }
}