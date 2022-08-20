package com.ets.onlinebiblioteka.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.viewmodels.EditProfileViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class EditProfileFragment : Fragment() {
    private val viewModel: EditProfileViewModel by viewModels()
    private lateinit var name: String
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var photoUrl: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name = it.getString("name").toString()
            username = it.getString("username").toString()
            email = it.getString("email").toString()
            photoUrl = it.getString("photo_url").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.edit_profile_btn_back).setOnClickListener {
            requireActivity().onBackPressed()
        }
        view.findViewById<MaterialButton>(R.id.edit_profile_btn_ponisti).setOnClickListener {
            requireActivity().onBackPressed()
        }

        val nameTxtInput = view.findViewById<TextInputLayout>(R.id.edit_profile_et_name)
        val emailTxtInput = view.findViewById<TextInputLayout>(R.id.edit_profile_et_email)
        val usernameTxtInput = view.findViewById<TextInputLayout>(R.id.edit_profile_et_username)
        var oldPassTxtInput = view.findViewById<TextInputLayout>(R.id.edit_profile_et_old_pass)
        var newPassTxtInput = view.findViewById<TextInputLayout>(R.id.edit_profile_et_new_pass)
        var newPassAgainTxtInput = view.findViewById<TextInputLayout>(R.id.edit_profile_et_new_pass_again)
        val progressBar = view.findViewById<ProgressBar>(R.id.edit_profile_progress_bar)

        nameTxtInput.editText!!.setText(name)
        emailTxtInput.editText!!.setText(email)
        usernameTxtInput.editText!!.setText(username)
        Glide.with(this)
            .load(photoUrl)
            .centerCrop()
            .placeholder(R.color.black)
            .into(view.findViewById(R.id.edit_profile_img))


        viewModel.failure().observe(viewLifecycleOwner) { failed ->
            if (failed) {
                Toast.makeText(requireContext(), "Failed to edit user", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }


        view.findViewById<Button>(R.id.edit_profile_btn_sacuvaj).setOnClickListener {
            progressBar.visibility = View.VISIBLE
            viewModel.editUser(
                nameTxtInput.editText!!.text.toString(),
                emailTxtInput.editText!!.text.toString(),
                usernameTxtInput.editText!!.text.toString(),
                oldPassTxtInput.editText!!.text.toString(),
                newPassTxtInput.editText!!.text.toString(),
                newPassAgainTxtInput.editText!!.text.toString()
            )
        }

        viewModel.getStatus().observe(viewLifecycleOwner) { status ->
            progressBar.visibility = View.GONE
            status?.let {
                when (it) {
                    EditProfileViewModel.EditProfileStatus.OLD_PASSWORD_INVALID -> {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "Old password invalid",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    EditProfileViewModel.EditProfileStatus.PASSWORD_CONFIRMATION_INVALID -> {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "Password confirmation does not match",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    EditProfileViewModel.EditProfileStatus.EMPTY_FIELDS -> {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "Please fill out every field",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    EditProfileViewModel.EditProfileStatus.SUCCESS -> {
                        view.findNavController().navigate(R.id.nav_action_edit_profil_to_moj_profil)
                    }
                }
            }
        }
    }
}