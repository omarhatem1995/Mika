package com.mika

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
       // setSupportActionBar()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_login) as NavHostFragment
        val navController = navHostFragment.navController
       // appBarConfiguration = AppBarConfiguration(navController.graph)
       // setupActionBarWithNavController(navController, appBarConfiguration)

        database = Firebase.database.reference
        writeNewUser("1","name","email")
    }

    fun writeNewUser(userId: String, name: String, email: String) {
      //  val user = User(name, email)

     //   database.child("users").child(userId).setValue(user)
    }
}