package com.ets.onlinebiblioteka.fragments.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.viewmodels.ForgotLoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class ForgotLoginFragment : Fragment() {
    private val viewModel: ForgotLoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.forgot_login_progress_bar)

        val txtInput = view.findViewById<TextInputLayout>(R.id.forgot_login_et_username_or_email)

        val radioGroup = view.findViewById<RadioGroup>(R.id.forgot_login_radio_group)
        radioGroup.check(R.id.forgot_login_radio_password)

        radioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.forgot_login_radio_password -> txtInput.setHint(R.string.username)
                R.id.forgot_login_radio_username -> txtInput.setHint(R.string.email)
            }
        }


        val submitBtn = view.findViewById<Button>(R.id.forgot_login_btn_submit)

        submitBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            when (radioGroup.checkedRadioButtonId) {
                R.id.forgot_login_radio_password -> {
                    viewModel.forgotPassword(txtInput.editText!!.text.toString())
                }
                R.id.forgot_login_radio_username -> {
                    viewModel.forgotUsername(txtInput.editText!!.text.toString())
                }
            }
        }

        viewModel.msg().observe(viewLifecycleOwner) { msg ->
            msg?.let {
                progressBar.visibility = View.GONE
                if (it == "success") {
                    when (radioGroup.checkedRadioButtonId) {
                        R.id.forgot_login_radio_password -> {
                            view.findNavController()
                                .navigate(R.id.nav_login_action_forgot_to_request_sent_password)
                        }
                        R.id.forgot_login_radio_username -> {
                            view.findNavController()
                                .navigate(R.id.nav_login_action_forgot_to_request_sent_username)
                        }
                    }
                } else {
                    var snackBarMsg = "Invalid username"
                    if (radioGroup.checkedRadioButtonId == R.id.forgot_login_radio_username) {
                        snackBarMsg = "Invalid email"
                    }

                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        snackBarMsg,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.failure().observe(viewLifecycleOwner) { failed ->
            if (failed) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Sending data failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val returnText = view.findViewById<TextView>(R.id.forgot_login_text_nazad)

        returnText.setOnClickListener {
            view.findNavController().navigate(R.id.nav_login_action_forgot_to_main)
        }
    }
}