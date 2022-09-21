package com.ets.onlinebiblioteka.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.Zahtjev
import com.ets.onlinebiblioteka.util.GlobalData
import com.google.android.material.chip.Chip


class MojiZahtjeviAdapter(
    val items: ArrayList<Zahtjev>,
    val context: Context,
    val onClick: (Zahtjev) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var img = itemView.findViewById<ImageView>(R.id.card_moji_zahtjevi_img)
        private var title = itemView.findViewById<TextView>(R.id.card_moji_zahtjevi_text_title)
        private var author = itemView.findViewById<TextView>(R.id.card_moji_zahtjevi_text_author)
        private var dateFrom = itemView.findViewById<TextView>(R.id.card_moji_zahtjevi_text_date_from)
        private var dateTo = itemView.findViewById<TextView>(R.id.card_moji_zahtjevi_text_date_to)
        private var type = itemView.findViewById<Chip>(R.id.card_moji_zahtjevi_chip)

        fun bind(position: Int) {
            val item = items[position]

            itemView.setOnClickListener {
                onClick(item)
            }

            Glide.with(context)
                .load(GlobalData.getImageUrl(item.book.photo))
                .centerCrop()
                .placeholder(R.color.black)
                .into(img)
            title.text = item.book.title
            if (item.book.authors.isNotEmpty()) {
                author.text = "by ${item.book.authors[0].name}"
            } else {
                author.text = ""
            }

            dateFrom.text = item.dateFrom.substring(0, 10)
            if (item.dateTo == "") {
                dateTo.setTextColor(ContextCompat.getColor(context, R.color.red))
                dateTo.setText(R.string.nije_vracena)
            } else {
                dateTo.setTextColor(ContextCompat.getColor(context, R.color.gray))
                dateTo.text = item.dateTo.substring(0, 10)
            }

            when(item.type) {
                "reservation rejected" -> {
                    val color = ContextCompat.getColor(context, R.color.red)
                    type.chipStrokeColor = ColorStateList.valueOf(color)
                    type.setTextColor(color)

                    type.setText(R.string.odbijeno)
                }
                "reservation" -> {
                    val color = ContextCompat.getColor(context, R.color.orange)
                    type.chipStrokeColor = ColorStateList.valueOf(color)
                    type.setTextColor(color)

                    type.setText(R.string.rezervacija)
                }
                "rent" -> {
                    val color = ContextCompat.getColor(context, R.color.green)
                    type.chipStrokeColor = ColorStateList.valueOf(color)
                    type.setTextColor(color)

                    type.setText(R.string.zaduzivanje)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.card_moji_zahtjevi, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
