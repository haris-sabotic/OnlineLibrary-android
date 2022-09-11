package com.ets.onlinebiblioteka.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.R


class SearchHistoryAdapter(
    val items: ArrayDeque<String>,
    val context: Context,
    val pasteButtonClick: (text: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var text = itemView.findViewById<TextView>(R.id.item_search_history_text)
        private var btn = itemView.findViewById<View>(R.id.item_search_history_btn)


        fun bind(position: Int) {
            val item = items[position]

            text.text = item

            btn.setOnClickListener {
                pasteButtonClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_search_history, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}