package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.adapters.MojiZahtjeviAdapter
import com.ets.onlinebiblioteka.viewmodels.MojiZahtjeviViewModel
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MojiZahtjeviFragment : Fragment() {
    private val viewModel: MojiZahtjeviViewModel by viewModels()

    private var snackbarMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load snackbar message to show from the previous fragment
        arguments?.let {
            snackbarMessage = it.getString("snackbar_message")
        }
    }

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
        val textNemaZahtjeva = view.findViewById<TextView>(R.id.moji_zahtjevi_text_nema_zahtjeva)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // show snackbar message from previous fragment and reload items
        snackbarMessage?.let {
            Snackbar.make(
                requireView(),
                it,
                Snackbar.LENGTH_SHORT
            ).setAction("OK") {
            }.show()

            progressBar.visibility = View.VISIBLE
            viewModel.loadItems("")

            snackbarMessage = null
        }

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
            // show text saying there's nothing to show if there isn't
            textNemaZahtjeva.visibility = if (it.size == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }

            recyclerView.adapter = MojiZahtjeviAdapter(it, requireContext()
            ) {
                val action = MojiZahtjeviFragmentDirections.navActionMojiZahtjeviToZahtjevInfo(it)
                view.findNavController().navigate(action)
            }
        }

        // reload items when you select a filter
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

        // scroll to the top
        fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
    }
}