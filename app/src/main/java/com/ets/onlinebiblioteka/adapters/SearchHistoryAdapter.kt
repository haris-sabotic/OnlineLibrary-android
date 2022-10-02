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
    // items that passed the text filter and what range matches the filter
    private var filteredItems = HashMap<Int, Pair<Int, Int>?>()
    init {
        // fill it out with all items on start
        items.forEachIndexed { index, s ->
            filteredItems[index] = Pair(0, 0)
        }
    }

    private inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var text = itemView.findViewById<TextView>(R.id.item_search_history_text)
        private var btn = itemView.findViewById<View>(R.id.item_search_history_btn)

        fun drop() {
            // set dimensions to 0, 0 and visibility to gone
            itemView.visibility = View.GONE
            itemView.layoutParams = RecyclerView.LayoutParams(0, 0 )
        }

        fun bind(position: Int) {
            // reset dimensions and visibility to fully show the item
            itemView.visibility = View.VISIBLE
            itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val item = items[position]

            val str = SpannableStringBuilder(item)
            // if there was a specific range the filter was found in, mark it as bold
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
            // if the item didn't pass the filter, don't show it
            (holder as ViewHolder).drop()
        } else {
            (holder as ViewHolder).bind(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun search(query: String, onItemNumChanged: (n: Int) -> Unit) {
        filteredItems.clear()

        if(query.isEmpty()) {
            // fill it out with all items if the query's empty
            items.forEachIndexed { index, s ->
                filteredItems[index] = Pair(0, 0)
            }
        } else {
            items.forEachIndexed { index, item ->
                // if the book contains the filter
                if (item.contains(query)) {
                    // find start and end index of the filter
                    val start = item.indexOf(query)
                    val end = start + query.length

                    filteredItems[index] = Pair(start, end)
                }
            }
        }

        onItemNumChanged(filteredItems.size)
        notifyDataSetChanged()
    }
}