package com.example.sendit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sendit.Login.LoginActivity
import com.example.sendit.Repos.SupabaseRepo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener


class MainActivity : AppCompatActivity() {
    private val sbRepo = SupabaseRepo()

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var signOutBtn: Button
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }

    override fun onStart() {
        super.onStart()
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        signOutBtn = findViewById(R.id.signout)
        button = findViewById(R.id.button)

        isLoggedUser()

        signOutBtn.setOnClickListener {
            signOut()
        }
    }

    private fun isLoggedUser(): Boolean {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)
        val acct: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)

        val prefs = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val id = prefs.getInt("idU", -1)

        if (acct != null) {
            val personName: String = acct.displayName ?: ""
            val personEmail: String = acct.email ?: ""
            name.text = personName
            email.text = personEmail
            return true
        }
        if (id != -1) {
            sbRepo.getUserById(lifecycleScope, id) {user ->
                name.text = user.username
                email.text = user.email
            }
            return true
        }
        return false
    }

    private fun signOut() {
        val prefs = this.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE).edit()
        prefs.putBoolean("SignedIn", false)
        prefs.putInt("idU", -1)
        prefs.apply()
        gsc.signOut().addOnCompleteListener(this, OnCompleteListener {
            finish()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        })
    }

}