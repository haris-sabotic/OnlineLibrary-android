package com.ets.onlinebiblioteka.fragments.main

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.adapters.AktivnostiAdapter
import com.ets.onlinebiblioteka.adapters.SearchHistoryAdapter
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.util.NavDrawerController
import com.google.gson.Gson


class SearchFragment : Fragment() {
    private lateinit var actionBar: ActionBar
    private lateinit var editText: EditText
    private lateinit var searchBtn: View
    private lateinit var recyclerView: RecyclerView

    private var searchHistory = ArrayDeque<String>()
    private val MAX_HISTORY_SIZE = 10

    companion object {
        val HISTORY_SHARED_PREFS_KEY = "SEARCH_HISTORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity() as NavDrawerController).setDrawerEnabled(false)

        actionBar = (requireActivity() as AppCompatActivity).supportActionBar!!
        actionBar.setCustomView(R.layout.action_bar_search)

        editText = actionBar.customView.findViewById(R.id.action_bar_search_et)
        searchBtn = actionBar.customView.findViewById(R.id.action_bar_search_btn)

        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowCustomEnabled(true)

        GlobalData.getSharedPreferences().getString(HISTORY_SHARED_PREFS_KEY, null)?.let {
            Log.d("HISTORY_ITEM", "...")
            searchHistory = Gson().fromJson(
                it,
                ArrayDeque::class.java
            ) as ArrayDeque<String>
        }

        for (item in searchHistory) {
            Log.d("HISTORY_ITEM", item)
        }
    }

    override fun onDestroy() {
        super.onResume()

        actionBar.setDisplayShowCustomEnabled(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editText.requestFocus()
        val inputMethodManager: InputMethodManager =
            activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        val textCaption = view.findViewById<TextView>(R.id.search_text_caption)

        editText.addTextChangedListener { text ->
            (recyclerView.adapter as SearchHistoryAdapter).search(text.toString()) { n ->
                textCaption.text = if (n == searchHistory.size) {
                    "Skorija pretraživanja"
                } else {
                    "Rezultati"
                }
            }
        }

        searchBtn.setOnClickListener { search() }
        editText.setOnEditorActionListener { _, _, _ ->
            search()
            false
        }

        recyclerView = view.findViewById(R.id.search_recycler_view_history)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = SearchHistoryAdapter(searchHistory, requireContext()) { item ->
            editText.setText(item)
        }
    }

    private fun search() {
        val text: String = editText.text.toString()
        if (text.isNotEmpty()) {
            searchHistory.addFirst(text)
            if (searchHistory.size > MAX_HISTORY_SIZE) {
                searchHistory.removeLast()
            }
            GlobalData.getSharedPreferences().edit().putString(
                HISTORY_SHARED_PREFS_KEY,
                Gson().toJson(searchHistory)
            ).commit()

            actionBar.setDisplayShowCustomEnabled(false)

            val inputMethodManager: InputMethodManager =
                activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)

            val action = SearchFragmentDirections.navActionSearchFragmentToKnjige(text)
            findNavController().navigate(action)
        }

    }
}