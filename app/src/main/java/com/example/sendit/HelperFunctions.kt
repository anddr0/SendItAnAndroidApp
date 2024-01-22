package com.example.sendit

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

class HelperFunctions {

    fun changeTVHeight(textView: TextView, height: Int? = null) {
        if (height == null) {
            val params = textView.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            textView.layoutParams = params
        }
        else {
            val params = textView.layoutParams
            params.height = height
            textView.layoutParams = params
        }
    }

   fun trimStr(editText: EditText): String {
        return editText.text.toString().trim()
   }

    fun saveUserLogging(view: View, idU: Int) {
        val prefs = view.context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE).edit()
        prefs.putBoolean("SignedIn", true)
        prefs.putInt("idU", idU)
        prefs.apply()
    }

    fun getLoggedUserId(view: View) : Int {
        return view.context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE).getInt("idU" ,-1)
    }

    fun generatePassword(length: Int): String {
        val uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercaseLetters = "abcdefghijklmnopqrstuvwxyz"
        val digits = "0123456789"
        val specialChars = "!@#$%^&*()-=_+[]{}|;:'<>,.?/~"

        val allChars = uppercaseLetters + lowercaseLetters + digits + specialChars

        if (length < 8) {
            throw IllegalArgumentException("Password length must be at least 8 characters long")
        }

        val password = buildString {
            append(uppercaseLetters.random())
            append(lowercaseLetters.random())
            append(digits.random())
            append(specialChars.random())

            repeat(length - 4) {
                append(allChars.random())
            }
        }

        return password.shuffle()
    }

    private fun String.shuffle(): String {
        val shuffledChars = this.toCharArray().toMutableList()
        shuffledChars.shuffle()
        return shuffledChars.joinToString("")
    }
}