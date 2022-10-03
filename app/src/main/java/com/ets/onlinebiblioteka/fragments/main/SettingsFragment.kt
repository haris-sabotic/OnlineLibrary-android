package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.viewmodels.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isNotifikacijeExpanded = false
        val btnNotifikacije = view.findViewById<RelativeLayout>(R.id.settings_dropdown_notifikacije)
        val btnNotifikacijeIcon = view.findViewById<ImageView>(R.id.settings_btn_expand)
        val notifikacijePreview = view.findViewById<TextView>(R.id.settings_dropdown_notifikacije_preview)
        val notifikacijeExpanded = view.findViewById<LinearLayout>(R.id.settings_dropdown_notifikacije_expanded)
        val btnDeleteIstorijaPretrazivanja = view.findViewById<Button>(R.id.settings_btn_delete_istorija_pretrazivanja)
        val btnDeleteListaZelja = view.findViewById<Button>(R.id.settings_btn_delete_lista_zelja)
        val switchNovaKnjiga = view.findViewById<SwitchMaterial>(R.id.settings_switch_nova)
        val radioGroupLanguage = view.findViewById<RadioGroup>(R.id.settings_radio_group_language)

        // load switch state
        switchNovaKnjiga.isChecked = viewModel.getNovaKnjiga()

        // expand/hide notification toggles
        btnNotifikacije.setOnClickListener {
            if (isNotifikacijeExpanded) {
                notifikacijeExpanded.visibility = View.GONE
                notifikacijePreview.visibility = View.VISIBLE
                btnNotifikacijeIcon.rotation = 0F
                isNotifikacijeExpanded = false
            } else {
                notifikacijePreview.visibility = View.GONE
                notifikacijeExpanded.visibility = View.VISIBLE
                btnNotifikacijeIcon.rotation = 180F
                isNotifikacijeExpanded = true
            }
        }

        // erase search history
        btnDeleteIstorijaPretrazivanja.setOnClickListener {
            GlobalData.getSharedPreferences()
                .edit()
                .remove(SearchFragment.HISTORY_SHARED_PREFS_KEY)
                .commit()

            btnDeleteIstorijaPretrazivanja.isEnabled = false

            Toast.makeText(
                requireContext(),
                R.string.istorija_pretrazivanja_uspjesno_obrisana,
                Toast.LENGTH_SHORT
            ).show()
        }

        // erase wishlist
        btnDeleteListaZelja.setOnClickListener {
            GlobalData.getSharedPreferences()
                .edit()
                .remove(ListaZeljaFragment.SHARED_PREFS_KEY)
                .commit()

            btnDeleteListaZelja.isEnabled = false

            Toast.makeText(
                requireContext(),
                R.string.lista_zelja_uspjesno_obrisana,
                Toast.LENGTH_SHORT
            ).show()
        }

        radioGroupLanguage.setOnCheckedChangeListener { _, i ->

        }

        switchNovaKnjiga.setOnCheckedChangeListener { _, checked ->
            viewModel.toggleNovaKnjiga(checked)
        }
    }
}