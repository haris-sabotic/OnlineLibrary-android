package com.ets.onlinebiblioteka.fragments.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.util.FilterModelController
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.viewmodels.BookDetailsViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class BookDetailsFragment : Fragment() {
    private val MAX_DESCRIPTION_LENGTH = 260

    private val viewModel: BookDetailsViewModel by viewModels()

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
    private lateinit var bottomSheetBookSpecs: ConstraintLayout
    private lateinit var bookSpecsCloseBtn: ImageView
    private lateinit var bookSpecsTextPageCount: TextView
    private lateinit var bookSpecsTextScript: TextView
    private lateinit var bookSpecsTextLanguage: TextView
    private lateinit var bookSpecsTextBinding: TextView
    private lateinit var bookSpecsTextFormat: TextView
    private lateinit var bookSpecsTextIsbn: TextView
    private lateinit var bookSpecsProgressBar: ProgressBar
    private lateinit var btnSave: LinearLayout
    private lateinit var btnSaveText: TextView
    private lateinit var btnSaveIcon: ImageView

    private var descriptionReadMoreShown = false
    private var bookSaved = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            bookData = it.getParcelable<Book>("BOOK_DATA")!!
        }

        GlobalData.getSharedPreferences().getString(ListaZeljaFragment.SHARED_PREFS_KEY, null)?.let { s ->
            val books = Gson().fromJson(
                s,
                object : TypeToken<MutableList<Book>>() {}.type
            ) as MutableList<Book>

            bookSaved = books.map { it.id == bookData.id }.contains(true)
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
        bottomSheetBookSpecs = view.findViewById(R.id.book_details_bottom_sheet_book_specs)
        bookSpecsCloseBtn = view.findViewById(R.id.book_details_specs_btn_close)
        bookSpecsTextPageCount = view.findViewById(R.id.book_details_specs_text_page_count)
        bookSpecsTextScript = view.findViewById(R.id.book_details_specs_text_script)
        bookSpecsTextLanguage = view.findViewById(R.id.book_details_specs_text_language)
        bookSpecsTextBinding = view.findViewById(R.id.book_details_specs_text_binding)
        bookSpecsTextFormat = view.findViewById(R.id.book_details_specs_text_format)
        bookSpecsTextIsbn = view.findViewById(R.id.book_details_specs_text_isbn)
        bookSpecsProgressBar = view.findViewById(R.id.book_details_specs_progress_bar)
        btnSave = view.findViewById(R.id.book_details_btn_save)
        btnSaveText = view.findViewById(R.id.book_details_btn_save_text)
        btnSaveIcon = view.findViewById(R.id.book_details_btn_save_icon)

        textTitle.text = bookData.title

        setupSaveBtn(bookSaved)

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
            val color = ContextCompat.getColor(requireContext(), R.color.red)
            chipAvailability.chipStrokeColor = ColorStateList.valueOf(color)
            chipAvailability.setTextColor(color)

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
                val action = BookDetailsFragmentDirections.navActionBookDetailsToAllBooks(
                    null,
                    SelectedFilters(
                        null,
                        mutableListOf(),
                        mutableListOf(),
                        mutableListOf(Pair(item.getChipId(), item.getChipText())),
                        null,
                        null,
                        null
                    )
                )

                findNavController().navigate(action)
            },
            "by "
        )

        setupListText(
            authorsText,
            bookData.authors,
            { item ->
                val action = BookDetailsFragmentDirections.navActionBookDetailsToAllBooks(
                    null,
                    SelectedFilters(
                        null,
                        mutableListOf(),
                        mutableListOf(),
                        mutableListOf(Pair(item.getChipId(), item.getChipText())),
                        null,
                        null,
                        null
                    )
                )

                findNavController().navigate(action)
            }
        )
        setupListText(
            categoriesText,
            bookData.categories,
            { item ->
                val action = BookDetailsFragmentDirections.navActionBookDetailsToAllBooks(
                    null,
                    SelectedFilters(
                        null,
                        mutableListOf(Pair(item.getChipId(), item.getChipText())),
                        mutableListOf(),
                        mutableListOf(),
                        null,
                        null,
                        null
                    )
                )

                findNavController().navigate(action)
            }
        )
        setupListText(
            genresText,
            bookData.genres,
            { item ->
                val action = BookDetailsFragmentDirections.navActionBookDetailsToAllBooks(
                    null,
                    SelectedFilters(
                        null,
                        mutableListOf(),
                        mutableListOf(Pair(item.getChipId(), item.getChipText())),
                        mutableListOf(),
                        null,
                        null,
                        null
                    )
                )

                findNavController().navigate(action)
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

        btnSpecs.setOnClickListener {
            bottomSheetBookSpecs.visibility = View.VISIBLE
            viewModel.loadSpecs(bookData.id)
        }

        bookSpecsCloseBtn.setOnClickListener {
            bottomSheetBookSpecs.visibility = View.GONE
        }

        viewModel.getSpecs().observe(viewLifecycleOwner) {
            it?.let { specs ->
                bookSpecsProgressBar.visibility = View.GONE

                bookSpecsTextPageCount.text = specs.pages
                bookSpecsTextScript.text = specs.script
                bookSpecsTextLanguage.text = specs.language
                bookSpecsTextBinding.text = specs.binding
                bookSpecsTextFormat.text = specs.format
                bookSpecsTextIsbn.text = specs.isbn
            }
        }


        btnSave.setOnClickListener {
            if (bookSaved) {
                unsaveBook()

                bookSaved = false
                setupSaveBtn(bookSaved)
            } else {
                saveBook()

                bookSaved = true
                setupSaveBtn(bookSaved)

                Snackbar.make(
                    view,
                    "Knjiga je sačuvana u listu želja!",
                    Snackbar.LENGTH_LONG
                ).setAction("Poništi!") {
                    unsaveBook()

                    bookSaved = false
                    setupSaveBtn(bookSaved)
                }.show()
            }
        }


        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupListText(
        textView: TextView,
        list: List<FilterModelController>,
        onItemClick: (id: FilterModelController) -> Unit,
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
                item.getChipText(),
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

    private fun setupSaveBtn(saved: Boolean) {
        if (saved) {
            btnSaveIcon.setImageResource(R.drawable.ic_saved)
            btnSaveText.text = "Sačuvano"
        } else {
            btnSaveIcon.setImageResource(R.drawable.ic_save)
            btnSaveText.text = "Sačuvaj"
        }
    }

    private fun saveBook() {
        var books = mutableListOf<Book>()

        GlobalData.getSharedPreferences().getString(ListaZeljaFragment.SHARED_PREFS_KEY, null)?.let {
            books = Gson().fromJson(
                it,
                object : TypeToken<MutableList<Book>>() {}.type
            ) as MutableList<Book>
        }

        books.add(bookData)

        GlobalData.getSharedPreferences().edit().putString(
            ListaZeljaFragment.SHARED_PREFS_KEY,
            Gson().toJson(books)
        ).commit()
    }
    private fun unsaveBook() {
        GlobalData.getSharedPreferences().getString(ListaZeljaFragment.SHARED_PREFS_KEY, null)?.let { s ->
            val books = Gson().fromJson(
                s,
                object : TypeToken<MutableList<Book>>() {}.type
            ) as MutableList<Book>

            books.removeIf { it.id == bookData.id }

            GlobalData.getSharedPreferences().edit().putString(
                ListaZeljaFragment.SHARED_PREFS_KEY,
                Gson().toJson(books)
            ).commit()
        }
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