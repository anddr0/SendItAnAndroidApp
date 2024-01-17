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
import com.example.sendit.HelperFunctions
import com.example.sendit.MainActivity
import com.example.sendit.R
import com.example.sendit.Repos.SupabaseRepo
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class SignupFragment : Fragment() {
    private val sbRepo = SupabaseRepo()
    private val helpersFuncs = HelperFunctions()

    private lateinit var btSignup: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPasswordRepeat: EditText
    private lateinit var tvMessage: TextView
    private lateinit var tvWarningEmail: TextView
    private lateinit var tvWarningPass: TextView
    private lateinit var tvWarningPassRepeat: TextView

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
        tvWarningEmail = view.findViewById(R.id.tv_warning_email)
        tvWarningPass = view.findViewById(R.id.tv_warning_pass)
        tvWarningPassRepeat = view.findViewById(R.id.tv_warning_pass_rep)

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
            if (validate()) {
                sbRepo.ifUserExists(lifecycleScope, etEmail.text.toString().trim()) {exists ->
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
                        helpersFuncs.changeTVHeight(tvMessage)
                    }
                    else {
//                        sbRepo.signUp(lifecycleScope, etEmail.text.toString().trim(), etPassword.text.toString().trim())
                        (activity as? LoginActivity)?.runToAnotherFragment(helpersFuncs.trimStr(etEmail), helpersFuncs.trimStr(etPassword))
                    }
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

    private fun validate(): Boolean {
        val email = showRequiredText(tvWarningEmail, emailValidate(helpersFuncs.trimStr(etEmail)), getString(R.string.warn_email))
        val pass = showRequiredText(tvWarningPass, passwordValidate(helpersFuncs.trimStr(etPassword)), getString(R.string.warn_pass))
        val passRep = showRequiredText(tvWarningPassRepeat,
            passwordRepeatValidate(
                helpersFuncs.trimStr(etPassword),
                helpersFuncs.trimStr(etPasswordRepeat)),
                getString(R.string.warn_pass_repeat) )
        return email && pass && passRep
    }

    private fun emailValidate(email: String): Boolean {
        val emailPattern = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailPattern.toRegex())
    }

    private fun passwordValidate(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isDigit() }) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isLowerCase() }) return false
        if (!password.any { !it.isLetterOrDigit() }) return false
        return true
    }

    private fun passwordRepeatValidate(password: String, passRepeat: String): Boolean {
        if (passRepeat != password) return false
        return true
    }

    private fun showRequiredText(textView: TextView, show: Boolean, text: String): Boolean {
        if (show) {
            helpersFuncs.changeTVHeight(textView, 0)
        }
        else {
            textView.text = text
            helpersFuncs.changeTVHeight(textView)
        }
        return show
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


