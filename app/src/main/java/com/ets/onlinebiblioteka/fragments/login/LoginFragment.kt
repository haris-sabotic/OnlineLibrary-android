package com.ets.onlinebiblioteka.fragments.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.ets.onlinebiblioteka.MainActivity
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.login_progress_bar)
        val forgotLoginText = view.findViewById<TextView>(R.id.login_text_ne_mogu_da_pristupim)
        val loginBtn = view.findViewById<Button>(R.id.login_btn_log_in)
        val etUsername: TextInputLayout = view.findViewById(R.id.login_et_username)
        val etPassword: TextInputLayout = view.findViewById(R.id.login_et_password)
        val checkRememberMe: CheckBox = view.findViewById(R.id.login_check_zapamti_me)
        val textRegister: TextView = view.findViewById(R.id.login_text_nemas_nalog)

        forgotLoginText.setOnClickListener {
            view.findNavController().navigate(R.id.nav_login_action_main_to_forgot)
        }

        textRegister.setOnClickListener {
            view.findNavController().navigate(R.id.nav_login_action_main_to_registration)
        }

        loginBtn.setOnClickListener {
            if (etUsername.isNotEmpty() && etPassword.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                viewModel.login(
                    etUsername.editText!!.text.toString(),
                    etPassword.editText!!.text.toString()
                )
            }
        }

        viewModel.loginResponse().observe(viewLifecycleOwner) { response ->
            response?.let {
                progressBar.visibility = View.GONE
                if (it.msg == "failure") {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        R.string.nevalidan_podaci_o_korisniki,
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    // save token(just for the current session, or in sharedPreferences)
                    GlobalData.setToken(it.token, checkRememberMe.isChecked)

                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
            }
        }

        viewModel.failure().observe(viewLifecycleOwner) { failed ->
            if (failed) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    R.string.slanje_podataka_neuspjesno,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}