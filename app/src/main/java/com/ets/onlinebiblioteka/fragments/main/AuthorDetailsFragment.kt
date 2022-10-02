package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.adapters.BooksAdapter
import com.ets.onlinebiblioteka.models.filters.Autor
import com.ets.onlinebiblioteka.util.ItemOffsetDecoration
import com.ets.onlinebiblioteka.viewmodels.AuthorDetailsViewModel
import com.google.android.material.snackbar.Snackbar

class AuthorDetailsFragment : Fragment() {
    private val MAX_DESCRIPTION_LENGTH = 260

    private val viewModel: AuthorDetailsViewModel by viewModels()

    private lateinit var authorData: Autor

    private lateinit var btnBack: ImageView
    private lateinit var textName: TextView
    private lateinit var textDescription: TextView
    private lateinit var btnReadMore: LinearLayout
    private lateinit var btnReadMoreText: TextView
    private lateinit var btnReadMoreIcon: ImageView
    private lateinit var textLabelMoreBooks: TextView
    private lateinit var recyclerMoreBooks: RecyclerView

    private var descriptionReadMoreShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            authorData = it.getParcelable<Autor>("AUTHOR_DATA")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_author_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack = view.findViewById(R.id.author_details_btn_back)
        textName = view.findViewById(R.id.author_details_text_name)
        textDescription = view.findViewById(R.id.author_details_text_description)
        btnReadMore = view.findViewById(R.id.author_details_btn_read_more)
        btnReadMoreText = view.findViewById(R.id.author_details_text_read_more)
        btnReadMoreIcon = view.findViewById(R.id.author_details_img_read_more)
        textLabelMoreBooks = view.findViewById(R.id.author_details_text_label_more_books)
        recyclerMoreBooks = view.findViewById(R.id.author_details_recycler_view_more_books)

        textName.text = authorData.name


        // load summary from author data
        var summary = if (authorData.biography == null) {
            "Autor nema opis"
        } else {
            authorData.biography!!.trim().removeHtmlPTag()
        }

        // shorten text
        if (summary.length > MAX_DESCRIPTION_LENGTH) {
            summary = summary.removeRange(MAX_DESCRIPTION_LENGTH, summary.length)
            summary += "..."
        } else {
            btnReadMore.visibility = View.GONE
        }

        textDescription.text = summary

        // Knjige od autora <b>${author name}</b>
        textLabelMoreBooks.text = SpannableStringBuilder("Knjige od autora ").append(
            "\"${authorData.name}\"",
            StyleSpan(android.graphics.Typeface.BOLD),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // show more/less when you click the button
        btnReadMore.setOnClickListener {
            if (descriptionReadMoreShown) {
                var summary = if (authorData.biography == null) {
                    "Autor nema opis"
                } else {
                    authorData.biography!!.trim().removeHtmlPTag()
                }

                // shorten text
                if (summary.length > MAX_DESCRIPTION_LENGTH) {
                    summary = summary.removeRange(MAX_DESCRIPTION_LENGTH, summary.length)
                    summary += "..."
                }

                textDescription.text = summary
                descriptionReadMoreShown = false

                btnReadMoreText.text = "Pročitaj više"
                btnReadMoreIcon.rotation = 90F
            } else {
                textDescription.text = if (authorData.biography == null) {
                    "Autor nema opis"
                } else {
                    authorData.biography!!.trim().removeHtmlPTag()
                }

                descriptionReadMoreShown = true

                btnReadMoreText.text = "Pročitaj manje"
                btnReadMoreIcon.rotation = -90F
            }
        }

        viewModel.loadMoreBooks(authorData.id)
        recyclerMoreBooks.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerMoreBooks.addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.item_offset)) // space items evenly
        viewModel.getMoreBooks().observe(viewLifecycleOwner) {
            recyclerMoreBooks.adapter = BooksAdapter(
                it.toMutableList(),
                requireContext(),
                { item ->
                    // open up book details on click
                    val action = AuthorDetailsFragmentDirections.navActionAuthorDetailsToBookDetails(item)
                    findNavController().navigate(action)
                },
                { available ->
                    // show message when clicking the book availability icon
                    Snackbar.make(
                        view,
                        if (available) {
                            "Knjiga je na raspolaganju"
                        } else {
                            "Knjiga je izdata, trenutno je nemamo u biblioteci"
                        },
                        Snackbar.LENGTH_SHORT
                    ).setAction("OK") {
                    }.show()
                },
                false
            )
        }
    }

    // <p>text goes here</p> -> text goes here
    // some author biographies get stored in the database with html p tags around them
    private fun String.removeHtmlPTag(): String {
        if (startsWith("<p>")) {
            var new = removeRange(0, "<p>".length)
            new = new.removeRange(new.length - "</p>".length, new.length)

            return new
        }

        return this
    }
}