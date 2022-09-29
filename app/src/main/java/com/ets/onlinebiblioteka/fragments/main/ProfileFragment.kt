package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadUser()

        val nameText = view.findViewById<TextView>(R.id.profile_text_ime)
        val jmbgText = view.findViewById<TextView>(R.id.profile_text_jmbg)
        val emailText = view.findViewById<TextView>(R.id.profile_text_email)
        val usernameText = view.findViewById<TextView>(R.id.profile_text_korisnicko_ime)
        var photoUrl = ""

        viewModel.failure().observe(viewLifecycleOwner) { failed ->
            if (failed) {
                Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }

        viewModel.getUser().observe(viewLifecycleOwner) { user ->
            user?.let {
                nameText.text = it.name
                jmbgText.text = it.jmbg
                emailText.text = it.email
                usernameText.text = it.username

                photoUrl = GlobalData.getImageUrl(it.photo)
                Glide.with(this)
                    .load(photoUrl)
                    .centerCrop()
                    .placeholder(R.color.black)
                    .into(view.findViewById(R.id.profile_img))

                view.findViewById<ProgressBar>(R.id.profile_progress_bar).visibility = View.GONE
            }
        }

        view.findViewById<ImageView>(R.id.profile_btn_back).setOnClickListener {
            requireActivity().onBackPressed()
        }

        view.findViewById<ImageView>(R.id.profile_btn_edit).setOnClickListener {
            val action = ProfileFragmentDirections.navActionMojProfilToEditProfil(
                nameText.text.toString(),
                usernameText.text.toString(),
                emailText.text.toString(),
                photoUrl
            )

            view.findNavController().navigate(action)
        }
    }
}