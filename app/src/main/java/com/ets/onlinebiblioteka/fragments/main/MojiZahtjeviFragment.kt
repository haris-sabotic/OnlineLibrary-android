package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.adapters.MojiZahtjeviAdapter
import com.ets.onlinebiblioteka.models.Zahtjev
import com.ets.onlinebiblioteka.util.ApiInterface
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.viewmodels.MojiZahtjeviViewModel
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MojiZahtjeviFragment : Fragment() {
    private val viewModel: MojiZahtjeviViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_moji_zahtjevi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.moji_zahtjevi_recycler_view)
        val fab = view.findViewById<FloatingActionButton>(R.id.moji_zahtjevi_fab_up)
        val chipGroup = view.findViewById<ChipGroup>(R.id.moji_zahtjevi_chip_group)
        val progressBar = view.findViewById<ProgressBar>(R.id.moji_zahtjevi_progress_bar)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        progressBar.visibility = View.VISIBLE
        viewModel.loadItems("")

        viewModel.failure().observe(viewLifecycleOwner) { failed ->
            if (failed) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT)
                    .show()

                chipGroup.clearCheck()
            }
        }

        viewModel.getItems().observe(viewLifecycleOwner) {
            progressBar.visibility = View.GONE
            recyclerView.adapter = MojiZahtjeviAdapter(it, requireContext())
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            progressBar.visibility = View.VISIBLE
            if (checkedIds.contains(R.id.moji_zahtjevi_chip_rezervacije)) {
                viewModel.loadItems("reservations")
            } else if (checkedIds.contains(R.id.moji_zahtjevi_chip_zaduzene_knjige)) {
                viewModel.loadItems("rents")
            } else if (checkedIds.contains(R.id.moji_zahtjevi_chip_vracene_knjige)) {
                viewModel.loadItems("returned")
            } else {
                viewModel.loadItems("")
            }
        }

        fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
    }
}