package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.adapters.BooksAdapter
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.util.ItemOffsetDecoration
import com.ets.onlinebiblioteka.util.NavDrawerController
import com.ets.onlinebiblioteka.viewmodels.AllBooksViewModel
import com.google.android.material.snackbar.Snackbar

class AllBooksFragment : Fragment() {
    private val viewModel: AllBooksViewModel by viewModels()

    private var textQuery: String? = null
    private var selectedFilters: SelectedFilters? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarBottom: ProgressBar

    private var page = 1
    private var canLoadMore = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity() as NavDrawerController).setDrawerEnabled(false)

        arguments?.let {
            textQuery = it.getString("TEXT_QUERY")
            selectedFilters = it.getParcelable("SELECTED_FILTERS")

            viewModel.loadBooks(1, textQuery, selectedFilters)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments == null) {
            viewModel.loadBooks(1, textQuery, selectedFilters)
        } else {
            arguments = null
        }

        return inflater.inflate(R.layout.fragment_all_books, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.all_books_recycler_view)
        progressBar = view.findViewById(R.id.all_books_progress_bar)
        progressBarBottom = view.findViewById(R.id.all_books_progress_bar_bottom)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.item_offset))
        recyclerView.adapter = BooksAdapter(
            mutableListOf(),
            requireContext(),
            { item ->
                val action = AllBooksFragmentDirections.navActionAllBooksToBookDetails(item)
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

        viewModel.getBooks().observe(viewLifecycleOwner) {
            it?.let { books ->
                page = books.currentPage
                canLoadMore = books.nextPageUrl != null

                progressBar.visibility = View.GONE
                progressBarBottom.visibility = View.GONE

                (recyclerView.adapter as BooksAdapter).addMoreBooks(books.data)

                viewModel.clearBooks()
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1) && canLoadMore
                    && progressBarBottom.visibility == View.GONE) {
                    progressBarBottom.visibility = View.VISIBLE
                    viewModel.loadBooks(page + 1, textQuery, selectedFilters)
                }
            }
        })
    }
}