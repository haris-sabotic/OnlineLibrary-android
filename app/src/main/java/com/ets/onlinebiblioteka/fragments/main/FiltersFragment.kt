package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.Paginated
import com.ets.onlinebiblioteka.util.FilterModelController
import com.ets.onlinebiblioteka.util.NavDrawerController
import com.ets.onlinebiblioteka.viewmodels.FiltersViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class FiltersFragment : Fragment() {
    private val viewModel: FiltersViewModel by viewModels()

    private lateinit var root: LinearLayout

    private var totalSelectedChipCount: Int = 0
    private var selectedKategorije = mutableSetOf<Int>()
    private var selectedZanrovi = mutableSetOf<Int>()
    private var selectedAutori = mutableSetOf<Int>()
    private var selectedIzdavaci = mutableSetOf<Int>()
    private var selectedPisma = mutableSetOf<Int>()
    private var selectedJezici = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        (requireActivity() as NavDrawerController).setDrawerEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.action_bar_with_confirm_icon, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(requireContext(), "Confirm", Toast.LENGTH_SHORT).show()
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

        setupChipGroup(
            view,
            R.id.filters_chip_group_dostupnost,
            listOf("Izdato", "Rezervisano", "Na raspolaganju")
        )
        setupFilter(
            "Kategorija",
            false,
            selectedKategorije,
            viewModel::loadKategorije,
            viewModel::getKategorije
        )
        setupFilter(
            "Zanr",
            false,
            selectedZanrovi,
            viewModel::loadZanrovi,
            viewModel::getZanrovi
        )
        setupFilter(
            "Autor",
            false,
            selectedAutori,
            viewModel::loadAutori,
            viewModel::getAutori
        )
        setupFilter(
            "Izdavac",
            true,
            selectedIzdavaci,
            viewModel::loadIzdavaci,
            viewModel::getIzdavaci
        )
        setupFilter(
            "Pismo",
            true,
            selectedPisma,
            viewModel::loadPisma,
            viewModel::getPisma
        )
        setupFilter(
            "Jezik",
            true,
            selectedJezici,
            viewModel::loadJezici,
            viewModel::getJezici
        )
    }

    fun setupChipGroup(view: View, chipGroupId: Int, chips: List<String>) {
        val chipGroup = view.findViewById<ChipGroup>(chipGroupId)

        for (chipText in chips) {
            val chip = layoutInflater.inflate(
                R.layout.chip_filter,
                chipGroup,
                false
            ) as Chip

            chip.apply {
                text = chipText
                chipGroup.addView(this)

                setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        totalSelectedChipCount += 1
                    } else {
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
        selectedChipsSet: MutableSet<Int>,
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

        btnPrev.setOnClickListener {
            getFun().value?.let { liveData ->
                if (liveData.prevPageUrl != null) {
                    loadFun(liveData.currentPage - 1)
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

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
                    if (selectedChipsSet.contains(controller.getChipId())) {
                        isChecked = true
                    }
                    setOnCheckedChangeListener { _, checked ->
                        if (checked) {
                            totalSelectedChipCount += 1
                            selectedChipsSet.add(controller.getChipId())
                        } else {
                            totalSelectedChipCount -= 1
                            selectedChipsSet.remove(controller.getChipId())
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