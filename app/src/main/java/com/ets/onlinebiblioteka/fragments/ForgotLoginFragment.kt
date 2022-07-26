package com.ets.onlinebiblioteka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.ets.onlinebiblioteka.R
import com.google.android.material.textfield.TextInputLayout

class ForgotLoginFragment : Fragment() {
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

        val txtInput = view.findViewById<TextInputLayout>(R.id.forgot_login_et_username_or_email)

        val radioGroup = view.findViewById<RadioGroup>(R.id.forgot_login_radio_group)
        radioGroup.check(R.id.forgot_login_radio_password)

        radioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.forgot_login_radio_password -> txtInput.setHint(R.string.username)
                R.id.forgot_login_radio_username -> txtInput.setHint(R.string.email)
            }
        }
    }
}