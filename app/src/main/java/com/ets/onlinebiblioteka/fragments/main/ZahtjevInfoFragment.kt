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

        val authorSpannableStr = SpannableStringBuilder("by ")
        var i = 0
        for (author in data.book.authors) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    val action = ZahtjevInfoFragmentDirections.navActionZahtjevInfoToAllBooks(
                        null,
                        SelectedFilters(
                            null,
                            mutableListOf(),
                            mutableListOf(),
                            mutableListOf(Pair(author.id, author.name)),
                            null,
                            null,
                            null
                        )
                    )

                    findNavController().navigate(action)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }

            authorSpannableStr.append(
                author.name,
                clickableSpan,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

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
                Toast.makeText(requireContext(), "Failed to send data", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.getMsg().observe(viewLifecycleOwner) {
            it?.let {
                val message = if (ponistiOrIzbrisi == "ponisti") {
                    "Rezervacija uspješno poništena!"
                } else {
                    "Transakcija uspješno obrisana!"
                }

                val action = ZahtjevInfoFragmentDirections
                    .navActionZahtjevInfoToMojiZahtjevi(message)
                findNavController().navigate(action)
            }
        }

        btnPonisti.setOnClickListener {
            if (btnDisabled) {
                Snackbar.make(
                    view,
                    "Ne možete obrisati transakciju, knjiga nije vraćena",
                    Snackbar.LENGTH_SHORT
                ).setAction("OK") {
                }.setAnchorView(view.findViewById(R.id.zahtjev_info_separator_bottom)).show()
            } else {
                val title = if (ponistiOrIzbrisi == "ponisti") {
                    "Poništi rezervaciju"
                } else {
                    "Izbriši transakciju"
                }
                val message = if (ponistiOrIzbrisi == "ponisti") {
                    "Da li ste sigurni da želite da poništite ovu rezervaciju?"
                } else {
                    "Da li ste sigurni da želite da obrišete ovu transakciju?"
                }
                val btnText = if(ponistiOrIzbrisi == "ponisti") {
                    "PONIŠTI"
                } else {
                    "IZBRIŠI"
                }

                AlertDialog.Builder(requireContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("NAZAD" ) { _, _ -> }
                    .setPositiveButton(btnText ) { _, _ ->
                        viewModel.izbrisi(data.id, data.type)
                    }.create().show()
            }
        }
    }
}