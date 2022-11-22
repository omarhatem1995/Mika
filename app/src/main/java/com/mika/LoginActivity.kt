package com.mika

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mika.databinding.ActivityLoginBinding
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginBinding
    private val AUTH_REQUEST_CODE = 1171
    private var firebaseAuth : FirebaseAuth? = null
    private lateinit var stateListener : FirebaseAuth.AuthStateListener
    private lateinit var providers : List<AuthUI.IdpConfig>
    override fun onStart() {
        super.onStart()
        firebaseAuth?.addAuthStateListener(stateListener)
    }

    override fun onStop() {
        firebaseAuth?.removeAuthStateListener(stateListener)
        super.onStop()

    }

    fun init(){
        providers = listOf(AuthUI.IdpConfig.GoogleBuilder().build(),
                          AuthUI.IdpConfig.EmailBuilder().build())
        stateListener = FirebaseAuth.AuthStateListener {
            var user : FirebaseUser? = firebaseAuth?.currentUser
            if(user != null){
                Toast.makeText(this,    " You are already", Toast.LENGTH_LONG).show()
            }else{
                startActivityForResult(AuthUI
                    .getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.drawable.mika)
                    .build(),AUTH_REQUEST_CODE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        init()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_login) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_login)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}