package com.ets.onlinebiblioteka.fragments.main

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.Zahtjev
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.viewmodels.ZahtjevInfoViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import java.util.*


class ZahtjevInfoFragment : Fragment() {
    private val viewModel: ZahtjevInfoViewModel by viewModels()
    private lateinit var data: Zahtjev

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            data = it.getParcelable("zahtjev")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_zahtjev_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textTitle = view.findViewById<TextView>(R.id.zahtjev_info_text_title)
        val textAuthor = view.findViewById<TextView>(R.id.zahtjev_info_text_author)
        val textDateFrom = view.findViewById<TextView>(R.id.zahtjev_info_text_date_from)
        val textDateTo = view.findViewById<TextView>(R.id.zahtjev_info_text_date_to)
        val textLibrarian = view.findViewById<TextView>(R.id.zahtjev_info_text_osoba)
        val textID = view.findViewById<TextView>(R.id.zahtjev_info_text_id)
        val img = view.findViewById<ImageView>(R.id.zahtjev_info_img)
        val chip = view.findViewById<Chip>(R.id.zahtjev_info_chip)
        val btnPonisti = view.findViewById<LinearLayout>(R.id.zahtjev_info_btn_ponisti)
        val textPonisti = btnPonisti.findViewById<TextView>(R.id.zahtjev_info_text_ponisti)

        textTitle.text = data.book.title
        textLibrarian.text = data.librarian
        textID.text = data.id

        var btnDisabled = false

        // show only the yyyy-mm-dd part of the date and a custom message with a different color
        // if there's no date
        textDateFrom.text = data.dateFrom.substring(0, 10)
        if (data.dateTo == "") {
            textDateTo.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            textDateTo.setText(R.string.nije_vracena)

            btnPonisti.alpha = 0.4F
            btnDisabled = true
        } else {
            textDateTo.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
            textDateTo.text = data.dateTo.substring(0, 10)

            btnPonisti.alpha = 1.0F
        }

        // show all authors as clickable links
        val authorSpannableStr = SpannableStringBuilder("by ")
        var i = 0
        for (author in data.book.authors) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    // show author details on click
                    val action = ZahtjevInfoFragmentDirections.navActionZahtjevInfoToAuthorDetails(author)

                    findNavController().navigate(action)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    // remove text underline
                    ds.isUnderlineText = false
                }
            }

            authorSpannableStr.append(
                author.name,
                clickableSpan,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // add comma between author names
            if (i != data.book.authors.size - 1) {
                authorSpannableStr.append(", ")
            }

            i += 1
        }
        textAuthor.movementMethod = LinkMovementMethod.getInstance()
        textAuthor.highlightColor = Color.TRANSPARENT
        textAuthor.text = authorSpannableStr

        Glide.with(this)
            .load(GlobalData.getImageUrl(data.book.photo))
            .centerCrop()
            .placeholder(R.color.black)
            .into(img)

        var ponistiOrIzbrisi = "ponisti"

        // set up chip style and text
        when (data.type) {
            "reservation rejected" -> {
                val color = ContextCompat.getColor(requireContext(), R.color.red)
                chip.chipStrokeColor = ColorStateList.valueOf(color)
                chip.setTextColor(color)

                chip.setText(R.string.odbijeno)

                textPonisti.setText(R.string.izbrisi_transakciju)
                ponistiOrIzbrisi = "izbrisi"
            }
            "reservation" -> {
                val color = ContextCompat.getColor(requireContext(), R.color.orange)
                chip.chipStrokeColor = ColorStateList.valueOf(color)
                chip.setTextColor(color)

                chip.setText(R.string.rezervacija)

                textPonisti.setText(R.string.ponisti_rezervaciju)
                ponistiOrIzbrisi = "ponisti"
            }
            "rent" -> {
                val color = ContextCompat.getColor(requireContext(), R.color.green)
                chip.chipStrokeColor = ColorStateList.valueOf(color)
                chip.setTextColor(color)

                chip.setText(R.string.zaduzivanje)

                textPonisti.setText(R.string.izbrisi_transakciju)
                ponistiOrIzbrisi = "izbrisi"
            }
        }

        viewModel.failure().observe(viewLifecycleOwner) { failed ->
            if (failed) {
                Toast.makeText(requireContext(), R.string.slanje_podataka_neuspjesno, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // show message on request deleted
        viewModel.getMsg().observe(viewLifecycleOwner) {
            it?.let {
                val message = if (ponistiOrIzbrisi == "ponisti") {
                    resources.getString(R.string.rezervacija_uspjesno_ponistena)
                } else {
                    resources.getString(R.string.transakcija_uspjesno_obrisana)
                }

                val action = ZahtjevInfoFragmentDirections
                    .navActionZahtjevInfoToMojiZahtjevi(message)
                findNavController().navigate(action)
            }
        }

        // delete request
        btnPonisti.setOnClickListener {
            if (btnDisabled) {
                // can't delete if book hasn't been returnes
                Snackbar.make(
                    view,
                    R.string.ne_mozete_obrisati_transakciju_knjiga_nije_vracena,
                    Snackbar.LENGTH_SHORT
                ).setAction("OK") {
                }.setAnchorView(view.findViewById(R.id.zahtjev_info_separator_bottom)).show()
            } else {
                // dialog asking for confirmation to delete
                val title = if (ponistiOrIzbrisi == "ponisti") {
                    resources.getString(R.string.ponisti_rezervaciju)
                } else {
                    resources.getString(R.string.izbrisi_transakciju)
                }
                val message = if (ponistiOrIzbrisi == "ponisti") {
                    resources.getString(R.string.da_li_ste_sigurni_da_zelite_da_ponistite_ovu_rezervaciju)
                } else {
                    resources.getString(R.string.da_li_ste_sigurni_da_zelite_da_obrisete_ovu_transakciju)
                }
                val btnText = if(ponistiOrIzbrisi == "ponisti") {
                    resources.getString(R.string.uppercase__ponisti)
                } else {
                    resources.getString(R.string.uppercase__obrisi)
                }

                AlertDialog.Builder(requireContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton(resources.getString(R.string.uppercase__nazad)) { _, _ -> }
                    .setPositiveButton(btnText ) { _, _ ->
                        viewModel.izbrisi(data.id, data.type)
                    }.create().show()
            }
        }
    }
}