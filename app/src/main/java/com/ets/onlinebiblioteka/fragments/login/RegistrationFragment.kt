package com.ets.onlinebiblioteka.fragments.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.ets.onlinebiblioteka.MainActivity
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.viewmodels.RegistrationViewModel
import com.google.android.material.textfield.TextInputLayout

class RegistrationFragment : Fragment() {
    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.registration_progress_bar)
        val registerBtn = view.findViewById<Button>(R.id.registration_btn_registration)
        val etName: TextInputLayout = view.findViewById(R.id.registration_et_name)
        val etJmbg: TextInputLayout = view.findViewById(R.id.registration_et_jmbg)
        val etUsername: TextInputLayout = view.findViewById(R.id.registration_et_username)
        val etEmail: TextInputLayout = view.findViewById(R.id.registration_et_email)
        val etPassword: TextInputLayout = view.findViewById(R.id.registration_et_password)
        val etPasswordConfirm: TextInputLayout = view.findViewById(R.id.registration_et_password_confirm)
        val checkRememberMe: CheckBox = view.findViewById(R.id.registration_check_zapamti_me)
        val textGoBack: TextView = view.findViewById(R.id.registration_text_back_to_login)


        // go back to login screen
        textGoBack.setOnClickListener {
            view.findNavController().navigate(R.id.nav_login_action_registration_to_main)
        }

        // send registration request
        registerBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            viewModel.register(
                etName.editText!!.text.toString(),
                etEmail.editText!!.text.toString(),
                etUsername.editText!!.text.toString(),
                etJmbg.editText!!.text.toString(),
                etPassword.editText!!.text.toString(),
                etPasswordConfirm.editText!!.text.toString(),
            )
        }

        viewModel.getStatus().observe(viewLifecycleOwner) { status ->
            progressBar.visibility = View.GONE
            status?.let {
                // handle all status messages
                when (it) {
                    RegistrationViewModel.RegistrationStatus.FAILURE -> {
                        Toast.makeText(requireContext(), R.string.registration_failed, Toast.LENGTH_SHORT).show()
                    }
                    RegistrationViewModel.RegistrationStatus.EMAIL_TAKEN -> {
                        Toast.makeText(requireContext(), R.string.email_taken, Toast.LENGTH_SHORT).show()
                    }
                    RegistrationViewModel.RegistrationStatus.USERNAME_TAKEN -> {
                        Toast.makeText(requireContext(), R.string.username_taken, Toast.LENGTH_SHORT).show()
                    }
                    RegistrationViewModel.RegistrationStatus.JMBG_TAKEN -> {
                        Toast.makeText(requireContext(), R.string.jmbg_taken, Toast.LENGTH_SHORT).show()
                    }
                    RegistrationViewModel.RegistrationStatus.PASSWORD_TOO_SHORT -> {
                        Toast.makeText(requireContext(), R.string.password_too_short, Toast.LENGTH_SHORT).show()
                    }
                    RegistrationViewModel.RegistrationStatus.PASSWORD_CONFIRMATION_INVALID -> {
                        Toast.makeText(requireContext(), R.string.potvrda_sifre_nevelidna, Toast.LENGTH_SHORT).show()
                    }
                    RegistrationViewModel.RegistrationStatus.INVALID_JMBG -> {
                        Toast.makeText(requireContext(), R.string.jmbg_should_contain_13_digits, Toast.LENGTH_SHORT).show()
                    }
                    RegistrationViewModel.RegistrationStatus.EMPTY_FIELDS -> {
                        Toast.makeText(requireContext(), R.string.ispunite_svako_polje, Toast.LENGTH_SHORT).show()
                    }
                    RegistrationViewModel.RegistrationStatus.SUCCESS -> {
                        // save token(just for the current session, or in sharedPreferences)
                        GlobalData.setToken(viewModel.token!!, checkRememberMe.isChecked)

                        startActivity(Intent(requireContext(), MainActivity::class.java))
                    }
                }
            }
        }
    }
}