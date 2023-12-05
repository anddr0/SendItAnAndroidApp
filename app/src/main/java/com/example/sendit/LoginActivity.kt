package com.example.sendit

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LoginActivity : AppCompatActivity() {

    lateinit var gso: GoogleSignInOptions
    lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso);

        val sign_btn: Button = findViewById(R.id.sign_button)
        sign_btn.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()

        val g_acc: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (g_acc != null) {
            navigateToSecondActivity()
        }

    }

    fun signIn() {
        val sing_in_intent: Intent = gsc.getSignInIntent()
        startActivityForResult(sing_in_intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                task.getResult(ApiException::class.java)
                navigateToSecondActivity()
            } catch (e: ApiException) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun navigateToSecondActivity() {
        finish()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }
}