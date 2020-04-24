package com.example.simplechat.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.simplechat.R
import com.example.simplechat.interfaces.NavigationHost
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment.view.*

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.login_fragment, container, false)

        // Set listener on Log-in Button
        val button = view.next_button
        button.setOnClickListener {
            val password = password_edit_text.text!!
            val email = email_edit_text.text!!
            val toast = Toast.makeText(this.context, "Wrong Credentials", 2)
            if(!isPasswordValid(password) || !isEmailValid(email) ){
                toast.show()
            } else{
                // Navigate to Next Activity
                (activity as NavigationHost).navigateTo(ChatroomFragment(), false)
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
        return text != null && text.toString() == "jarjit@mail.com"
    }

}
