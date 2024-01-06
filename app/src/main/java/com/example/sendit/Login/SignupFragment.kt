package com.example.sendit.Login

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
import com.example.sendit.R
import com.example.sendit.Repos.SupabaseRepo

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class SignupFragment : Fragment() {
    private val sbRepo = SupabaseRepo()
    private lateinit var btSignup: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPasswordRepeat: EditText
    private lateinit var tvMessage: TextView

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
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btSignup = view.findViewById(R.id.bt_signup)
        etEmail = view.findViewById(R.id.et_email)
        etPassword = view.findViewById(R.id.et_password)
        etPasswordRepeat = view.findViewById(R.id.et_password_repeat)
        tvMessage = view.findViewById(R.id.tvMessage)

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLoginButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLoginButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        etPasswordRepeat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLoginButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btSignup.setOnClickListener {
            sbRepo.isUserExists(lifecycleScope, etEmail.text.toString()) {exists ->
                if (exists) {
                    val spannable = SpannableString(getString(R.string.user_registered))
                    val signInSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
                            viewPager?.currentItem = 0
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = ContextCompat.getColor(requireContext(), R.color.md_theme_light_primary)
                        }
                    }
                    spannable.setSpan(signInSpan, spannable.length - "SignIn".length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tvMessage.text = spannable
                    tvMessage.movementMethod = LinkMovementMethod.getInstance()
                }
                else {
                    Toast.makeText(view.context, "You are not registered :)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateLoginButtonState() {
        val emailFilled = etEmail.text.toString().trim().isNotEmpty()
        val passwordFilled = etPassword.text.toString().trim().isNotEmpty()
        val passwordFilledRepeat = etPasswordRepeat.text.toString().trim().isNotEmpty()
        btSignup.isEnabled = emailFilled && passwordFilled && passwordFilledRepeat
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}