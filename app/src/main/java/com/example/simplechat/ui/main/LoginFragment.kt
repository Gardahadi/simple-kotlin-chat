package com.example.simplechat.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplechat.R
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment.view.*

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

//    private lateinit var viewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        // Snippet from "Navigate to the next Fragment" section goes here.

        val view = inflater.inflate(R.layout.login_fragment, container, false)

        // Set listener on Next Button
        val next = view.next_button
        next.setOnClickListener {
            if(!isPasswordValid(password_edit_text.text!!) && !isEmailValid(email_edit_text.text!!) ){
                password_text_input.error = "Log in info is wrong"
            } else{
                password_text_input.error = null
                // Navigate to Next Activity

            }


        }

        return view
    }

    // Hardcoded validation function for password
    private fun isPasswordValid(text: Editable?): Boolean {
        return text != null && text.toString() == "123456"
    }

    // Hardcoded validation function for email
    private fun isEmailValid(text: Editable?) : Boolean {
        return text != null && text.toString() == "jarjit"
    }


}
