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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.adapters.AktivnostiAdapter
import com.ets.onlinebiblioteka.adapters.MojiZahtjeviAdapter
import com.ets.onlinebiblioteka.viewmodels.AktivnostiViewModel

class AktivnostiFragment : Fragment() {
    private val viewModel: AktivnostiViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_aktivnosti, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.aktivnosti_recycler_view)
        val textNoActivities = view.findViewById<TextView>(R.id.aktivnosti_text_no_activities)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.loadItems()

        viewModel.failure().observe(viewLifecycleOwner) { failed ->
            if (failed) {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.getItems().observe(viewLifecycleOwner) {
            textNoActivities.visibility = if (it.size == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }

            recyclerView.adapter = AktivnostiAdapter(it, requireContext())
        }
    }
}