package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.Paginated
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.util.FilterModelController
import com.ets.onlinebiblioteka.util.NavDrawerController
import com.ets.onlinebiblioteka.viewmodels.FiltersViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class FiltersFragment : Fragment() {
    private val viewModel: FiltersViewModel by viewModels()

    var textQuery: String? = null

    private lateinit var root: LinearLayout

    private var totalSelectedChipCount: Int = 0
    private var selectedDostupnost: String? = null
    private var selectedKategorije = mutableSetOf<Pair<Int, String>>()
    private var selectedZanrovi = mutableSetOf<Pair<Int, String>>()
    private var selectedAutori = mutableSetOf<Pair<Int, String>>()
    private var selectedIzdavaci = mutableSetOf<Pair<Int, String>>()
    private var selectedPisma = mutableSetOf<Pair<Int, String>>()
    private var selectedJezici = mutableSetOf<Pair<Int, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        (requireActivity() as NavDrawerController).setDrawerEnabled(false)

        // get text query from previous fragment(SearchFragment) so that you can pass it back unchanged
        arguments?.let {
            textQuery = it.getString("TEXT_QUERY")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.action_bar_with_confirm_icon, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != R.id.menu_item_confirm) {
            return super.onOptionsItemSelected(item)
        }

        // when you press the confirm button

        // prepare category, genre and author filters
        val categories = if (selectedKategorije.isNotEmpty()) {
            selectedKategorije.toList()
        } else {
            listOf()
        }
        val genres = if (selectedZanrovi.isNotEmpty()) {
            selectedZanrovi.toList()
        } else {
            listOf()
        }
        val authors = if (selectedAutori.isNotEmpty()) {
            selectedAutori.toList()
        } else {
            listOf()
        }

        // prepare publisher, script and language filters
        val publisher = if (selectedIzdavaci.isNotEmpty()) {
            selectedIzdavaci.toList()[0]
        } else {
            null
        }
        val script = if (selectedPisma.isNotEmpty()) {
            selectedPisma.toList()[0]
        } else {
            null
        }
        val language = if (selectedJezici.isNotEmpty()) {
            selectedJezici.toList()[0]
        } else {
            null
        }

        val selectedFilters = SelectedFilters(
            selectedDostupnost,
            categories.toMutableList(),
            genres.toMutableList(),
            authors.toMutableList(),
            publisher,
            script,
            language
        )

        // go back to previous screen with the selected filters
        val action = FiltersFragmentDirections.navActionFiltersToKnjige(selectedFilters, textQuery)
        findNavController().navigate(action)

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // hide confirm button in action bar at the start(0 filters selected)
        setMenuVisibility(false)

        root = view.findViewById(R.id.filters_root)

        // set up availability filters
        setupChipGroup(
            view,
            R.id.filters_chip_group_dostupnost,
            listOf(resources.getString(R.string.izdato), resources.getString(R.string.rezervisano), resources.getString(R.string.na_raspolaganju))
        ) { v ->
            selectedDostupnost = v
        }

        // set up every other filter
        setupFilter(
            resources.getString(R.string.kategorija),
            false,
            selectedKategorije,
            viewModel::loadKategorije,
            viewModel::getKategorije
        )
        setupFilter(
            resources.getString(R.string.zanr),
            false,
            selectedZanrovi,
            viewModel::loadZanrovi,
            viewModel::getZanrovi
        )
        setupFilter(
            resources.getString(R.string.autor),
            false,
            selectedAutori,
            viewModel::loadAutori,
            viewModel::getAutori
        )
        setupFilter(
            resources.getString(R.string.izdavac),
            true,
            selectedIzdavaci,
            viewModel::loadIzdavaci,
            viewModel::getIzdavaci
        )
        setupFilter(
            resources.getString(R.string.pismo),
            true,
            selectedPisma,
            viewModel::loadPisma,
            viewModel::getPisma
        )
        setupFilter(
            resources.getString(R.string.jezik),
            true,
            selectedJezici,
            viewModel::loadJezici,
            viewModel::getJezici
        )
    }

    fun setupChipGroup(view: View, chipGroupId: Int, chips: List<String>, onChecked: (String?) -> Unit) {
        val chipGroup = view.findViewById<ChipGroup>(chipGroupId)

        // add new chip for every item to the chip group dynamically
        for (chipText in chips) {
            val chip = layoutInflater.inflate(
                R.layout.chip_filter,
                chipGroup,
                false
            ) as Chip

            chip.apply {
                text = chipText
                chipGroup.addView(this)

                setOnCheckedChangeListener { v, checked ->
                    if (checked) {
                        onChecked(v.text.toString())
                        totalSelectedChipCount += 1
                    } else {
                        onChecked(null)
                        totalSelectedChipCount -= 1
                    }

                    // show/hide confirm button in action bar
                    if (totalSelectedChipCount > 0) {
                        setMenuVisibility(true)
                    } else {
                        setMenuVisibility(false)
                    }
                }
            }
        }
    }

    fun <T> setupFilter(
        name: String,
        singleSelection: Boolean,
        selectedChipsSet: MutableSet<Pair<Int, String>>,
        loadFun: (page: Int) -> Unit,
        getFun: () -> LiveData<Paginated<T>>
    ) {
        val itemView = layoutInflater.inflate(R.layout.item_filter_select, null)
        root.addView(itemView)

        val itemRoot = itemView.findViewById<LinearLayout>(R.id.filter_select_root)
        val chipGroup = itemView.findViewById<ChipGroup>(R.id.filter_select_chip_group)
        val btnExpand = itemView.findViewById<ImageView>(R.id.filter_select_btn_expand)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.filter_select_progress_bar)
        val btnPrev = itemView.findViewById<ImageView>(R.id.filter_select_btn_prev)
        val btnNext = itemView.findViewById<ImageView>(R.id.filter_select_btn_next)
        val textName = itemView.findViewById<TextView>(R.id.filter_select_text_name)
        val scrollView = itemView.findViewById<HorizontalScrollView>(R.id.filter_select_chip_group_scroll_view)

        textName.text = name
        chipGroup.isSingleSelection = singleSelection

        loadFun(1)

        btnExpand.setOnClickListener {
            chipGroup.isSingleLine = !chipGroup.isSingleLine

            // hide/show arrows for moving between pages
            if (chipGroup.isSingleLine) {
                btnPrev.visibility = View.GONE
                btnNext.visibility = View.GONE
            } else {
                btnPrev.visibility = View.VISIBLE
                btnNext.visibility = View.VISIBLE
            }

            progressBar.visibility = View.VISIBLE

            // refresh chip group (also make sure it always shows the first page in singleline mode)
            chipGroup.removeAllViews()
            loadFun(1)
        }

        // go back to previous page in expanded mode
        btnPrev.setOnClickListener {
            getFun().value?.let { liveData ->
                if (liveData.prevPageUrl != null) {
                    loadFun(liveData.currentPage - 1)
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

        // go to next page in expanded mode
        btnNext.setOnClickListener {
            getFun().value?.let { liveData ->
                if (liveData.nextPageUrl != null) {
                    loadFun(liveData.currentPage + 1)
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

        getFun().observe(viewLifecycleOwner) {
            progressBar.visibility = View.GONE
            chipGroup.removeAllViews()

            // enable/disable prev/next arrows
            if (it.prevPageUrl == null) {
                btnPrev.alpha = 0.6F
            } else {
                btnPrev.alpha = 1.0F
            }
            if (it.nextPageUrl == null) {
                btnNext.alpha = 0.6F
            } else {
                btnNext.alpha = 1.0F
            }

            // enable/disable horizontal scrolling based on whether the filters are expanded or not
            if (chipGroup.isSingleLine) {
                itemRoot.removeView(chipGroup)
                scrollView.visibility = View.VISIBLE
                scrollView.removeAllViews()
                scrollView.addView(chipGroup)
            } else {
                scrollView.removeView(chipGroup)
                scrollView.visibility = View.GONE
                itemRoot.removeView(chipGroup)
                itemRoot.addView(chipGroup, itemRoot.indexOfChild(scrollView))
            }

            // add new chip for every item to the chip group dynamically
            for (filter in it.data) {
                val chip = layoutInflater.inflate(
                    R.layout.chip_filter,
                    chipGroup,
                    false
                ) as Chip

                chip.apply {
                    val controller = filter as FilterModelController

                    text = controller.getChipText()
                    chipGroup.addView(this)

                    // handle selection
                    for (pair in selectedChipsSet) {
                        if (pair.first == controller.getChipId()) {
                            isChecked = true
                            break
                        }
                    }
                    setOnCheckedChangeListener { _, checked ->
                        if (checked) {
                            totalSelectedChipCount += 1
                            selectedChipsSet.add(Pair(controller.getChipId(), controller.getChipText()))
                        } else {
                            totalSelectedChipCount -= 1
                            selectedChipsSet.removeIf { pair ->
                                pair.first == controller.getChipId()
                            }
                        }

                        // show/hide confirm button in action bar
                        if (totalSelectedChipCount > 0) {
                            setMenuVisibility(true)
                        } else {
                            setMenuVisibility(false)
                        }
                    }
                }
            }
        }
    }
}