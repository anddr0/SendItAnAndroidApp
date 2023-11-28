package com.example.sendit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {

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
        sign_btn.setOnClickListener{
            signIn()
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
                // Get the signed-in account
                val account = task.getResult(ApiException::class.java)

                // Now you can use the account to access user details
                // For example, you can get the user's email:
                val email = account?.email

                // Continue with whatever you need to do after sign-in
                navigateToSecondActivity()
            } catch (e: ApiException) {
                // The ApiException will be thrown if sign-in was not successful
                Toast.makeText(applicationContext, "Sign-in failed. Error code: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun navigateToSecondActivity() {
        finish()
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
    }
}