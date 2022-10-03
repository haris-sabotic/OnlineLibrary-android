package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.ModelCardController
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.viewmodels.KnjigeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

class KnjigeFragment : Fragment() {
    private val viewModel: KnjigeViewModel by activityViewModels()

    private var textQuery: String? = null

    private lateinit var selectedFiltersChipGroup: ChipGroup
    private lateinit var filtersBtn: LinearLayout
    private lateinit var filtersBtnText: TextView
    private lateinit var filtersBtnIcon: View
    private lateinit var resultsTitle: TextView
    private lateinit var btnClearQuery: ImageView
    private lateinit var countText: TextView

    private lateinit var booksGridLayout: GridLayout
    private lateinit var booksBtnMore: LinearLayout

    private lateinit var categoriesGridLayout: GridLayout
    private lateinit var categoriesBtnMore: LinearLayout

    private lateinit var genresGridLayout: GridLayout
    private lateinit var genresBtnMore: LinearLayout

    private lateinit var scrollView: ScrollView
    private lateinit var fab: FloatingActionButton
    private lateinit var progressBar: ProgressBar

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

        progressBar = view.findViewById(R.id.knjige_progress_bar)

        if (arguments != null) {
            textQuery = arguments!!.getString("TEXT_QUERY")

            val filters: SelectedFilters? = arguments!!.getParcelable("SELECTED_FILTERS")
            viewModel.setSelectedFilters(filters)
        } else {
            viewModel.search(resources, textQuery)
            progressBar.visibility = View.VISIBLE
        }


        selectedFiltersChipGroup = view.findViewById(R.id.knjige_chip_group)
        filtersBtn = view.findViewById(R.id.knjige_btn_filters)
        filtersBtnText = filtersBtn.findViewById(R.id.knjige_btn_filters_text)
        filtersBtnIcon = filtersBtn.findViewById(R.id.knjige_btn_filters_icon)
        resultsTitle = view.findViewById(R.id.knjige_text_results_title)
        btnClearQuery = view.findViewById(R.id.knjige_btn_clear_search_query)
        countText = view.findViewById(R.id.knjige_text_count)

        booksGridLayout = view.findViewById(R.id.knjige_grid_layout_books)
        booksBtnMore = view.findViewById(R.id.knjige_btn_more_books)

        categoriesGridLayout = view.findViewById(R.id.knjige_grid_layout_kategorije)
        categoriesBtnMore = view.findViewById(R.id.knjige_btn_more_kategorije)

        genresGridLayout = view.findViewById(R.id.knjige_grid_layout_zanrovi)
        genresBtnMore = view.findViewById(R.id.knjige_btn_more_zanrovi)

        scrollView = view.findViewById(R.id.knjige_scroll_view)
        fab = view.findViewById(R.id.knjige_fab_up)

        if (viewModel.getCategories().value == null) {
            viewModel.loadCategories()
        }
        if (viewModel.getGenres().value == null) {
            viewModel.loadGenres()
        }

        if (textQuery != null) {
            resultsTitle.text = "${resources.getString(R.string.rezultati_za)} \"${textQuery}\""
            btnClearQuery.visibility = View.VISIBLE
        } else {
            resultsTitle.text = resources.getString(R.string.popularne_knjige)
            btnClearQuery.visibility = View.GONE
        }

        // clear search query and search books again
        btnClearQuery.setOnClickListener {
            resultsTitle.text = resources.getString(R.string.popularne_knjige)
            btnClearQuery.visibility = View.GONE
            textQuery = null
            viewModel.search(resources, textQuery)
            progressBar.visibility = View.VISIBLE
        }

        var selectedFiltersInitialObserve = true
        viewModel.getSelectedFilters().observe(viewLifecycleOwner) { selectedFilters ->
            selectedFiltersChipGroup.removeAllViews()

            if (!selectedFiltersInitialObserve) {
                viewModel.search(resources, textQuery)
                progressBar.visibility = View.VISIBLE
            } else {
                selectedFiltersInitialObserve = false
            }

            if (selectedFilters == null || selectedFilters.isEmpty()) {
                filtersBtnText.text = resources.getString(R.string.filters)
                filtersBtnIcon.setBackgroundResource(R.drawable.ic_filters_button_arrow)
                filtersBtnIcon.layoutParams.width = 8F.asDp()
                filtersBtnIcon.layoutParams.height = 12F.asDp()
                filtersBtn.setOnClickListener {
                    val action = KnjigeFragmentDirections.navActionKnjigeToFilters(textQuery)
                    findNavController().navigate(action)
                }
            } else {
                filtersBtnText.text = resources.getString(R.string.ukloni_filtere)
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
                progressBar.visibility = View.GONE

                // "23 knjige", "37 knjiga"
                val count = books.count.toString()
                countText.text = when (count[count.length - 1]) {
                    '2', '3', '4' -> {
                        if (count.endsWith("12") or
                            count.endsWith("13") or
                            count.endsWith("14")) {
                            "${resources.getString(R.string.ukupno__)} ${books.count} ${resources.getString(R.string.knjiga)}"
                        } else {
                            "${resources.getString(R.string.ukupno__)} ${books.count} ${resources.getString(R.string.__knjige)}"
                        }
                    }
                    else -> "${resources.getString(R.string.ukupno__)} ${books.count} ${resources.getString(R.string.knjiga)}"
                }

                if (books.nextPageUrl != null) {
                    booksBtnMore.visibility = View.VISIBLE
                } else {
                    booksBtnMore.visibility = View.GONE
                }

                setupGrid(
                    booksGridLayout,
                    R.layout.card_book,
                    books.data,
                    { item ->
                        val action = KnjigeFragmentDirections.navActionKnjigeToBookDetails(item)
                        findNavController().navigate(action)
                    },
                    { available ->
                        Snackbar.make(
                            view,
                            if (available) {
                                R.string.knjiga_je_na_raspolaganju
                            } else {
                                R.string.knjiga_je_izdata_trenutno_je_nemamo_u_biblioteci
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
                Toast.makeText(
                    requireContext(),
                    R.string.ucitavanje_podataka_neuspjesno,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.getCategories().observe(viewLifecycleOwner) {
            it?.let { categories ->
                if (categories.nextPageUrl != null) {
                    categoriesBtnMore.visibility = View.VISIBLE
                }

                setupGrid(
                    categoriesGridLayout,
                    R.layout.card_category_genre,
                    categories.data,
                    { item ->
                        val action = KnjigeFragmentDirections.navActionKnjigeToAllBooks(
                            null,
                            SelectedFilters(
                                null,
                                mutableListOf(Pair(item.getChipId(), item.getChipText())),
                                mutableListOf(),
                                mutableListOf(),
                                null,
                                null,
                                null
                            )
                        )

                        findNavController().navigate(action)
                    },
                    { available ->
                    }
                )
            }
        }

        viewModel.getGenres().observe(viewLifecycleOwner) {
            it?.let { genres ->
                if (genres.nextPageUrl != null) {
                    genresBtnMore.visibility = View.VISIBLE
                }

                setupGrid(
                    genresGridLayout,
                    R.layout.card_category_genre,
                    genres.data,
                    { item ->
                        val action = KnjigeFragmentDirections.navActionKnjigeToAllBooks(
                            null,
                            SelectedFilters(
                                null,
                                mutableListOf(),
                                mutableListOf(Pair(item.getChipId(), item.getChipText())),
                                mutableListOf(),
                                null,
                                null,
                                null
                            )
                        )

                        findNavController().navigate(action)
                    },
                    { available ->
                    }
                )
            }
        }

        booksBtnMore.setOnClickListener {
            val action = KnjigeFragmentDirections.navActionKnjigeToAllBooks(textQuery, viewModel.getSelectedFilters().value)
            findNavController().navigate(action)
        }

        categoriesBtnMore.setOnClickListener {
            val action = KnjigeFragmentDirections.navActionKnjigeToAllCategoriesOrGenres("categories")
            findNavController().navigate(action)
        }
        genresBtnMore.setOnClickListener {
            val action = KnjigeFragmentDirections.navActionKnjigeToAllCategoriesOrGenres("genres")
            findNavController().navigate(action)
        }

        fab.setOnClickListener {
            scrollView.smoothScrollTo(0, 0)
        }
    }

    private fun <T> setupGrid(
        grid: GridLayout,
        cardLayout: Int,
        items: List<ModelCardController<T>>,
        onCardClick: (item: T) -> Unit,
        onAvailabilityIconClick: (available: Boolean) -> Unit
    ) {
        grid.removeAllViews()

        var i = 0
        for (item in items) {
            val itemView = layoutInflater.inflate(cardLayout, null)

            val params = GridLayout.LayoutParams()
            if (items.size > 1) {
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1F)
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1F)
                params.setGravity(Gravity.CENTER_HORIZONTAL)
            }
            grid.addView(itemView, params)

            item.bind(requireContext(), itemView, onCardClick, onAvailabilityIconClick)

            i += 1
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

    // convert float to dp units
    private fun Float.asDp(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            resources.displayMetrics
        ).roundToInt()
    }
}