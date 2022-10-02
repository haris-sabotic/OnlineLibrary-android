package com.ets.onlinebiblioteka.adapters

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.set
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.R


class SearchHistoryAdapter(
    val items: ArrayDeque<String>,
    val context: Context,
    val pasteButtonClick: (text: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var filteredItems = HashMap<Int, Pair<Int, Int>?>()
    init {
        items.forEachIndexed { index, s ->
            filteredItems[index] = Pair(0, 0)
        }
    }

    private inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var text = itemView.findViewById<TextView>(R.id.item_search_history_text)
        private var btn = itemView.findViewById<View>(R.id.item_search_history_btn)

        fun drop() {
            itemView.visibility = View.GONE
            itemView.layoutParams = RecyclerView.LayoutParams(0, 0 )
        }

        fun bind(position: Int) {
            itemView.visibility = View.VISIBLE
            itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val item = items[position]

            val str = SpannableStringBuilder(item)
            filteredItems[position]?.let {
                str[it.first, it.second] = StyleSpan(android.graphics.Typeface.BOLD)
            }
            text.text = str

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
        if (filteredItems[position] == null) {
            (holder as ViewHolder).drop()
        } else {
            (holder as ViewHolder).bind(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun search(query: String) {
        filteredItems.clear()

        if(query.isEmpty()) {
            items.forEachIndexed { index, s ->
                filteredItems[index] = Pair(0, 0)
            }
        } else {
            items.forEachIndexed { index, item ->
                if (item.contains(query)) {
                    val start = item.indexOf(query)
                    val end = start + query.length

                    filteredItems[index] = Pair(start, end)
                }
            }
        }

        notifyDataSetChanged()
    }
}