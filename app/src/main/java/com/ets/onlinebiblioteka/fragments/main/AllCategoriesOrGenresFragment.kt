package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.MainActivity
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.adapters.CategoriesOrGenresAdapter
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.util.ItemOffsetDecoration
import com.ets.onlinebiblioteka.util.NavDrawerController
import com.ets.onlinebiblioteka.viewmodels.AllCategoriesOrGenresViewModel

class AllCategoriesOrGenresFragment : Fragment() {
    private val viewModel: AllCategoriesOrGenresViewModel by viewModels()

    private var categoriesOrGenres = "categories"

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarBottom: ProgressBar

    private var page = 1
    private var canLoadMore = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity() as NavDrawerController).setDrawerEnabled(false)

        arguments?.let {
            categoriesOrGenres = it.getString("CATEGORIES_OR_GENRES")!!

            viewModel.loadData(1, categoriesOrGenres)
        }

        (requireActivity() as MainActivity).setTitle(
            if (categoriesOrGenres == "categories") {
                "Kategorije"
            } else {
                "Žanrovi"
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments == null) {
            viewModel.loadData(1, categoriesOrGenres)
        } else {
            arguments = null
        }

        return inflater.inflate(R.layout.fragment_all_categories_or_genres, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.all_categories_or_genres_recycler_view)
        progressBar = view.findViewById(R.id.all_categories_or_genres_progress_bar)
        progressBarBottom = view.findViewById(R.id.all_categories_or_genres_progress_bar_bottom)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.item_offset))
        recyclerView.adapter = CategoriesOrGenresAdapter(
            mutableListOf(),
            requireContext()
        ) { item ->
            val selectedFilters = SelectedFilters(
                null,
                mutableListOf(), mutableListOf(), mutableListOf(),
                null, null, null
            )

            if (categoriesOrGenres == "categories") {
                selectedFilters.categories = mutableListOf(Pair(item.id, item.name))
            } else if (categoriesOrGenres == "genres") {
                selectedFilters.genres = mutableListOf(Pair(item.id, item.name))
            }

            val action =
                AllCategoriesOrGenresFragmentDirections.navActionAllCategoriesOrGenresToAllBooks(
                    null, selectedFilters
                )

            findNavController().navigate(action)
        }

        viewModel.getData().observe(viewLifecycleOwner) {
            it?.let { data ->
                page = data.currentPage
                canLoadMore = data.nextPageUrl != null

                progressBar.visibility = View.GONE
                progressBarBottom.visibility = View.GONE

                (recyclerView.adapter as CategoriesOrGenresAdapter).addMoreItems(data.data)

                viewModel.clearData()
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1) && canLoadMore
                    && progressBarBottom.visibility == View.GONE) {
                    progressBarBottom.visibility = View.VISIBLE
                    viewModel.loadData(page + 1, categoriesOrGenres)
                }
            }
        })
    }
}