package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.adapters.BooksAdapter
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.util.ItemOffsetDecoration
import com.ets.onlinebiblioteka.viewmodels.KnjigeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

class KnjigeFragment : Fragment() {
    private val viewModel: KnjigeViewModel by viewModels()

    private var textQuery: String? = null

    private lateinit var selectedFiltersChipGroup: ChipGroup
    private lateinit var filtersBtn: LinearLayout
    private lateinit var filtersBtnText: TextView
    private lateinit var filtersBtnIcon: View
    private lateinit var resultsTitle: TextView
    private lateinit var countText: TextView
    private lateinit var booksRecyclerView: RecyclerView
    private lateinit var booksProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(false)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.action_bar_with_search_icon, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        findNavController().navigate(R.id.nav_action_knjige_to_search_fragment)
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_knjige, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            textQuery = arguments!!.getString("TEXT_QUERY")

            val filters: SelectedFilters? = arguments!!.getParcelable("SELECTED_FILTERS")
            viewModel.setSelectedFilters(filters)
        } else {
            viewModel.search(textQuery)
        }


        selectedFiltersChipGroup = view.findViewById(R.id.knjige_chip_group)
        filtersBtn = view.findViewById(R.id.knjige_btn_filters)
        filtersBtnText = filtersBtn.findViewById(R.id.knjige_btn_filters_text)
        filtersBtnIcon = filtersBtn.findViewById(R.id.knjige_btn_filters_icon)
        resultsTitle = view.findViewById(R.id.knjige_text_results_title)
        countText = view.findViewById(R.id.knjige_text_count)
        booksRecyclerView = view.findViewById(R.id.knjige_recycler_view_books)
        booksProgressBar = view.findViewById(R.id.knjige_progress_bar_books)

        booksRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        booksRecyclerView.addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.item_offset))

        if (textQuery != null) {
            resultsTitle.text = "Rezultati za \"${textQuery}\""
        } else {
            resultsTitle.text = "Popularne knjige"
        }

        var selectedFiltersInitialObserve = true
        viewModel.getSelectedFilters().observe(viewLifecycleOwner) { selectedFilters ->
            selectedFiltersChipGroup.removeAllViews()

            if (!selectedFiltersInitialObserve) {
                booksProgressBar.visibility = View.VISIBLE
                viewModel.search(textQuery)
            } else {
                selectedFiltersInitialObserve = false
            }

            if (selectedFilters == null || selectedFilters.isEmpty()) {
                filtersBtnText.text = "Filters"
                filtersBtnIcon.setBackgroundResource(R.drawable.ic_filters_button_arrow)
                filtersBtnIcon.layoutParams.width = 8F.asDp()
                filtersBtnIcon.layoutParams.height = 12F.asDp()
                filtersBtn.setOnClickListener {
                    val action = KnjigeFragmentDirections.navActionKnjigeToFilters(textQuery)
                    findNavController().navigate(action)
                }
            } else {
                filtersBtnText.text = "Remove filters"
                filtersBtnIcon.setBackgroundResource(R.drawable.ic_x)
                filtersBtnIcon.layoutParams.width = 14F.asDp()
                filtersBtnIcon.layoutParams.height = 14F.asDp()
                filtersBtn.setOnClickListener {
                    viewModel.setSelectedFilters(null)
                }

                setupSelectedFiltersAvailability(selectedFilters)

                setupSelectedFiltersSingleValue(
                    selectedFilters,
                    {
                        selectedFilters.publisher
                    },
                    { v ->
                        selectedFilters.publisher = v
                    },
                )
                setupSelectedFiltersSingleValue(
                    selectedFilters,
                    {
                        selectedFilters.script
                    },
                    { v ->
                        selectedFilters.script = v
                    },
                )
                setupSelectedFiltersSingleValue(
                    selectedFilters,
                    {
                        selectedFilters.language
                    },
                    { v ->
                        selectedFilters.language = v
                    },
                )

                setupSelectedFiltersList(selectedFilters, selectedFilters.categories)
                setupSelectedFiltersList(selectedFilters, selectedFilters.genres)
                setupSelectedFiltersList(selectedFilters, selectedFilters.authors)

            }
        }

        viewModel.getBooks().observe(viewLifecycleOwner) {
            it?.let { books ->
                booksProgressBar.visibility = View.GONE

                // "23 knjige", "37 knjiga"
                val count = books.count.toString()
                countText.text = when (count[count.length - 1]) {
                    '2', '3', '4' -> {
                        if (count.endsWith("12") or
                            count.endsWith("13") or
                            count.endsWith("14")) {
                            "Ukupno ${books.count} knjiga"
                        } else {
                            "Ukupno ${books.count} knjige"
                        }
                    }
                    else -> "Ukupno ${books.count} knjiga"
                }

                booksRecyclerView.adapter = BooksAdapter(
                    books.data,
                    requireContext(),
                    { itemIndex ->
                        Toast.makeText(
                            requireContext(),
                            "Clicked book ${books.data[itemIndex].title}",
                            Toast.LENGTH_SHORT
                        ).show()
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
                    }
                )
            }
        }

        viewModel.getSearchFailure().observe(viewLifecycleOwner) { failed ->
            if (failed) {
                booksProgressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Failed to load books",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupSelectedFiltersAvailability(selectedFilters: SelectedFilters) {
        selectedFilters.availability?.let { availability ->
            val chip = layoutInflater.inflate(
                R.layout.chip_selected_filter,
                selectedFiltersChipGroup,
                false
            ) as Chip

            chip.apply {
                text = availability
                selectedFiltersChipGroup.addView(this)

                setOnCloseIconClickListener {
                    selectedFilters.availability = null
                    viewModel.setSelectedFilters(selectedFilters)
                }
            }
        }
    }

    private fun setupSelectedFiltersSingleValue(
        selectedFilters: SelectedFilters,
        getField: () -> Pair<Int, String>?,
        setField: (v: Pair<Int, String>?) -> Unit
    ) {
        getField()?.let {
            val chip = layoutInflater.inflate(
                R.layout.chip_selected_filter,
                selectedFiltersChipGroup,
                false
            ) as Chip

            chip.apply {
                text = it.second
                selectedFiltersChipGroup.addView(this)

                setOnCloseIconClickListener {
                    setField(null)
                    viewModel.setSelectedFilters(selectedFilters)
                }
            }
        }
    }

    private fun setupSelectedFiltersList(
        selectedFilters: SelectedFilters,
        list: MutableList<Pair<Int, String>>
    ) {
        val viewAndListIds = hashMapOf<Int, Int>()

        var i = 0
        for (pair in list) {
            val chip = layoutInflater.inflate(
                R.layout.chip_selected_filter,
                selectedFiltersChipGroup,
                false
            ) as Chip

            chip.apply {
                text = pair.second
                selectedFiltersChipGroup.addView(this)

                id = View.generateViewId()
                viewAndListIds[id] = i

                setOnCloseIconClickListener {
                    list.removeAt(viewAndListIds[it.id]!!)
                    viewModel.setSelectedFilters(selectedFilters)
                }
            }

            i += 1
        }
    }

    private fun Float.asDp(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            resources.displayMetrics
        ).roundToInt()
    }
}