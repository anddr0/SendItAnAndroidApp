package com.example.sendit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sendit.Login.LoginActivity
import com.example.sendit.Repos.SupabaseRepo
import com.example.sendit.databinding.ActivityNavMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener

class NavMainActivity : AppCompatActivity() {
    private val sbRepo = SupabaseRepo()

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)
        binding = ActivityNavMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_nav_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }

    override fun onStart() {
        super.onStart()
//        isLoggedUser()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_signout -> {
                signOut()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_nav_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun signOut() {
        val prefs = this.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE).edit()
        prefs.putBoolean("SignedIn", false)
        prefs.putInt("idU", -1)
        prefs.apply()
        gsc.signOut().addOnCompleteListener(this, OnCompleteListener {
            finish()
            startActivity(Intent(this@NavMainActivity, LoginActivity::class.java))
        })
    }

//    private fun isLoggedUser(): Boolean {
//
//        val acct: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
//
//        val prefs = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
//        val id = prefs.getInt("idU", -1)
//
//        if (acct != null) {
//            val personName: String = acct.displayName ?: ""
//            val personEmail: String = acct.email ?: ""
//            name.text = personName
//            email.text = personEmail
//            return true
//        }
//        if (id != -1) {
//            sbRepo.getUserById(lifecycleScope, id) {user ->
//                name.text = user.username
//                email.text = user.email
//            }
//            return true
//        }
//        return false
//    }
}