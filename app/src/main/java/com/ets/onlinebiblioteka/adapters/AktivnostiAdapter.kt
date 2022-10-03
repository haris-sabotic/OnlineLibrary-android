package com.ets.onlinebiblioteka.adapters

import android.content.Context
import android.graphics.Typeface
import android.icu.lang.UProperty.INT_START
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.Aktivnost
import com.ets.onlinebiblioteka.util.GlobalData


class AktivnostiAdapter(
    val items: ArrayList<Aktivnost>,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textType = itemView.findViewById<TextView>(R.id.item_aktivnosti_text_type)
        private var textDescription = itemView.findViewById<TextView>(R.id.item_aktivnosti_text_description)
        private var textDate = itemView.findViewById<TextView>(R.id.item_aktivnosti_text_date)
        private var img = itemView.findViewById<ImageView>(R.id.item_aktivnosti_img)


        fun bind(position: Int) {
            val item = items[position]

            Glide.with(context)
                .load(GlobalData.getImageUrl(item.photo))
                .centerCrop()
                .placeholder(R.color.black)
                .into(img)

            textDate.text = item.date.substring(0, 10)

            val text = "${item.librarian} ${context.getString(R.string.vam_je_izdao_knjigu)} ${item.book}"
            val str = SpannableStringBuilder(text)
            // mark librarian name as bold
            str.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                item.librarian.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // mark book title as bold
            str.setSpan(
                StyleSpan(Typeface.BOLD),
                text.length - item.book.length,
                text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textDescription.setText(str, TextView.BufferType.SPANNABLE)


            when (item.type) {
                "reservation" -> textType.setText(R.string.rezervacija_knjige)
                "rent" -> textType.setText(R.string.izdavanje_knjige)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_aktivnosti, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}