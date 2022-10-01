package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.util.GlobalData

class SettingsFragment : Fragment() {
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

        btnDeleteIstorijaPretrazivanja.setOnClickListener {
            GlobalData.getSharedPreferences()
                .edit()
                .remove(SearchFragment.HISTORY_SHARED_PREFS_KEY)
                .commit()

            btnDeleteIstorijaPretrazivanja.isEnabled = false

            Toast.makeText(
                requireContext(),
                "Istorija pretraživanja uspješno obrisana",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnDeleteListaZelja.setOnClickListener {
            GlobalData.getSharedPreferences()
                .edit()
                .remove(ListaZeljaFragment.SHARED_PREFS_KEY)
                .commit()

            btnDeleteListaZelja.isEnabled = false

            Toast.makeText(
                requireContext(),
                "Lista želja uspješno obrisana",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}