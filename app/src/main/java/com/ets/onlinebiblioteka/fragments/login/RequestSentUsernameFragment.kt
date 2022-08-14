package com.ets.onlinebiblioteka.fragments.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import com.ets.onlinebiblioteka.R

class RequestSentUsernameFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_request_sent_username, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backText = view.findViewById<TextView>(R.id.request_sent_username_text_povratak)

        backText.setOnClickListener {
            view.findNavController().navigate(R.id.nav_login_action_request_sent_username_to_main)
        }
    }
}