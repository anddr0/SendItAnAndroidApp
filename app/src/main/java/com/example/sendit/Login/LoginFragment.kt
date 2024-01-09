package com.example.sendit.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.sendit.MainActivity
import com.example.sendit.R
import com.example.sendit.Repos.SupabaseRepo


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LoginFragment : Fragment() {
    private val sbRepo = SupabaseRepo()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btLogin: Button
    private lateinit var passFrgt: TextView
    private lateinit var tvWarning1: TextView
    private lateinit var tvWarning2: TextView

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etEmail = view.findViewById(R.id.et_login)
        etPassword = view.findViewById(R.id.et_password)
        passFrgt = view.findViewById(R.id.pass_frgt)
        btLogin = view.findViewById(R.id.bt_login)
        tvWarning1 = view.findViewById(R.id.tv_warning1)
        tvWarning2 = view.findViewById(R.id.tv_warning2)

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLoginButtonState()
            }
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLoginButtonState()
            }
        })


        btLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            sbRepo.isUserExists(lifecycleScope, email, pass) { exists ->
                if (exists) {
                    navigateToSecondActivity()
                    view.context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE).edit().putBoolean("SignedIn", true).apply()
                } else {
                    Toast.makeText(view.context, "You are not registered :)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateLoginButtonState() {
        val emailCorrect = emailValidate(etEmail.text.toString().trim())
        val passwordCorrect = passwordValidate(etPassword.text.toString().trim())
        btLogin.isEnabled = emailCorrect && passwordCorrect
    }

    private fun emailValidate(email: String): Boolean {
        return true
    }
    private fun passwordValidate(password: String): Boolean {
        return true
    }

    private fun navigateToSecondActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}