package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var textNoBooks: TextView

    private var page = 1
    private var canLoadMore = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity() as NavDrawerController).setDrawerEnabled(false)

        // load search query and filters passed from previous fragment
        arguments?.let {
            textQuery = it.getString("TEXT_QUERY")
            selectedFilters = it.getParcelable("SELECTED_FILTERS")

            // load books right away
            viewModel.loadBooks(1, textQuery, selectedFilters)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // reload books when going back to this fragment over the backstack
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
        textNoBooks = view.findViewById(R.id.all_books_text_no_books)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.item_offset))
        recyclerView.adapter = BooksAdapter(
            mutableListOf(),
            requireContext(),
            { item ->
                // open up book details on click
                val action = AllBooksFragmentDirections.navActionAllBooksToBookDetails(item)
                findNavController().navigate(action)
            },
            { available ->
                // show message when clicking the book availability icon
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

                // show text saying there's no books if there are none
                textNoBooks.visibility =
                    if ((recyclerView.adapter as BooksAdapter).items.size == 0) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
        }

        // load more books when you scroll to the bottom(if possible)
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