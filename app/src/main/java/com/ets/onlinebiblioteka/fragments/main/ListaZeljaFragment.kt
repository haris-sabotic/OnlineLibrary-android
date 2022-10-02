package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.adapters.BooksAdapter
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.util.ItemOffsetDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListaZeljaFragment : Fragment() {
    companion object {
        val SHARED_PREFS_KEY = "LISTA_ZELJA"
    }

    private var books = mutableListOf<Book>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        GlobalData.getSharedPreferences().getString(SHARED_PREFS_KEY, null)?.let {
            books = Gson().fromJson(
                it,
                object : TypeToken<MutableList<Book>>() {}.type
            ) as MutableList<Book>
        }

        return inflater.inflate(R.layout.fragment_lista_zelja, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.lista_zelja_recycler_view)
        val textNoBooks = view.findViewById<TextView>(R.id.lista_zelja_text_no_books)

        textNoBooks.visibility = if (books.size == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.item_offset))
        recyclerView.adapter = BooksAdapter(
            books.reversed().toMutableList(),
            requireContext(),
            { item ->
                val action = ListaZeljaFragmentDirections.navActionListaZeljaToBookDetails(item)
                findNavController().navigate(action)
            },
            { available ->
                Snackbar.make(
                    view,
                    if (available) {
                        "Knjiga je na raspolaganju"
                    } else {
                        "Knjiga je izdata, trenutno je nemamo u biblioteci"
                    },
                    Snackbar.LENGTH_SHORT
                ).setAction("OK") {
                }.show()
            },
            true
        )
    }
}