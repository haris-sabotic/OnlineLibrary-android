package com.ets.onlinebiblioteka.fragments.main

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.util.GlobalData
import com.google.android.material.chip.Chip

val MAX_DESCRIPTION_LENGTH = 260

class BookDetailsFragment : Fragment() {
    private lateinit var bookData: Book

    private lateinit var btnBack: ImageView
    private lateinit var img: ImageView
    private lateinit var textTitle: TextView
    private lateinit var textAuthor: TextView
    private lateinit var chipAvailability: Chip
    private lateinit var textDescription: TextView
    private lateinit var btnReadMore: LinearLayout
    private lateinit var btnReadMoreText: TextView
    private lateinit var btnReadMoreIcon: ImageView
    private lateinit var quantityText: TextView
    private lateinit var categoriesText: TextView
    private lateinit var genresText: TextView
    private lateinit var authorsText: TextView
    private lateinit var publisherText: TextView
    private lateinit var publishYearText: TextView
    private lateinit var btnSpecs: RelativeLayout

    private var descriptionReadMoreShown = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            bookData = it.getParcelable<Book>("BOOK_DATA")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack = view.findViewById(R.id.book_details_btn_back)
        img = view.findViewById(R.id.book_details_img)
        textTitle = view.findViewById(R.id.book_details_text_title)
        textAuthor = view.findViewById(R.id.book_details_text_author)
        chipAvailability = view.findViewById(R.id.book_details_chip)
        textDescription = view.findViewById(R.id.book_details_text_description)
        btnReadMore = view.findViewById(R.id.book_details_btn_read_more)
        btnReadMoreText = view.findViewById(R.id.book_details_text_read_more)
        btnReadMoreIcon = view.findViewById(R.id.book_details_img_read_more)
        quantityText = view.findViewById(R.id.book_details_text_availability)
        categoriesText = view.findViewById(R.id.book_details_text_categories)
        genresText = view.findViewById(R.id.book_details_text_genres)
        authorsText = view.findViewById(R.id.book_details_text_authors)
        publisherText = view.findViewById(R.id.book_details_text_publisher)
        publishYearText = view.findViewById(R.id.book_details_text_publish_year)
        btnSpecs = view.findViewById(R.id.book_details_btn_specs)

        textTitle.text = bookData.title

        if (bookData.authors.isNotEmpty()) {
            textAuthor.text = "by ${bookData.authors[0]}"
        } else {
            textAuthor.text = ""
        }

        quantityText.text = if (bookData.quantity.toString().endsWith('1')
                                and !bookData.quantity.toString().endsWith("11")) {
            "${bookData.quantity} komad"
        } else {
            "${bookData.quantity} komada"
        }

        publishYearText.text = bookData.publishYear

        Glide.with(requireContext())
            .load(GlobalData.getImageUrl(bookData.photo))
            .centerCrop()
            .placeholder(R.color.black)
            .into(img)

        if (!bookData.available) {
            chipAvailability.setChipStrokeColorResource(R.color.red)
            chipAvailability.setTextColor(R.color.red)
            chipAvailability.setText(R.string.izdato)
        }

        var summary = bookData.summary.trim().removeHtmlPTag()

        // shorten text
        if (summary.length > MAX_DESCRIPTION_LENGTH) {
            summary = summary.removeRange(MAX_DESCRIPTION_LENGTH, summary.length)
            summary += "..."
        } else {
            btnReadMore.visibility = View.GONE
        }

        textDescription.text = summary

        setupListText(
            textAuthor,
            bookData.authors,
            { item ->
                Toast.makeText(requireContext(), "Text: $item", Toast.LENGTH_SHORT).show()
            },
            "by "
        )

        setupListText(
            authorsText,
            bookData.authors,
            { item ->
                Toast.makeText(requireContext(), "Text: $item", Toast.LENGTH_SHORT).show()
            }
        )
        setupListText(
            categoriesText,
            bookData.categories,
            { item ->
                Toast.makeText(requireContext(), "Text: $item", Toast.LENGTH_SHORT).show()
            }
        )
        setupListText(
            genresText,
            bookData.genres,
            { item ->
                Toast.makeText(requireContext(), "Text: $item", Toast.LENGTH_SHORT).show()
            }
        )

        btnReadMore.setOnClickListener {
            if (descriptionReadMoreShown) {
                var summary = bookData.summary.trim().removeHtmlPTag()

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
                textDescription.text = bookData.summary.trim().removeHtmlPTag()
                descriptionReadMoreShown = true

                btnReadMoreText.text = "Pročitaj manje"
                btnReadMoreIcon.rotation = -90F
            }
        }


        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupListText(
        textView: TextView,
        list: List<String>,
        onItemClick: (text: String) -> Unit,
        startText: String = ""
    ) {
        val spannableString = SpannableStringBuilder(startText)
        var i = 0
        for (item in list) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    onItemClick(item)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }

            spannableString.append(
                item,
                clickableSpan,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (i != list.size - 1) {
                spannableString.append(", ")
            }

            i += 1
        }
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
        textView.text = spannableString
    }

    private fun String.removeHtmlPTag(): String {
        if (startsWith("<p>")) {
            var new = removeRange(0, "<p>".length)
            new = new.removeRange(new.length - "</p>".length, new.length)

            return new
        }

        return this
    }
}