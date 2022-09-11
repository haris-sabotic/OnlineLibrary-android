package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.viewmodels.KnjigeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlin.math.roundToInt

class KnjigeFragment : Fragment() {
    private val viewModel: KnjigeViewModel by viewModels()

    private lateinit var selectedFiltersChipGroup: ChipGroup
    private lateinit var filtersBtn: LinearLayout
    private lateinit var filtersBtnText: TextView
    private lateinit var filtersBtnIcon: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            viewModel.setSelectedFilters(it.getParcelable("SELECTED_FILTERS"))
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.action_bar_with_search_icon, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(requireContext(), "Search", Toast.LENGTH_SHORT).show()
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

        selectedFiltersChipGroup = view.findViewById(R.id.knjige_chip_group)
        filtersBtn = view.findViewById(R.id.knjige_btn_filters)
        filtersBtnText = filtersBtn.findViewById(R.id.knjige_btn_filters_text)
        filtersBtnIcon = filtersBtn.findViewById(R.id.knjige_btn_filters_icon)

        viewModel.getSelectedFilters().observe(viewLifecycleOwner) { selectedFilters ->
            selectedFiltersChipGroup.removeAllViews()

            if (selectedFilters == null || selectedFilters.isEmpty()) {
                filtersBtnText.text = "Filters"
                filtersBtnIcon.setBackgroundResource(R.drawable.ic_filters_button_arrow)
                filtersBtnIcon.layoutParams.width = 8F.asDp()
                filtersBtnIcon.layoutParams.height = 12F.asDp()
                filtersBtn.setOnClickListener {
                    findNavController().navigate(R.id.nav_action_knjige_to_filters)
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