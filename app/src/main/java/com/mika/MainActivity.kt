package com.mika

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mika.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{

    lateinit var appBarconfig : AppBarConfiguration
    lateinit var binding : ActivityMainBinding
    lateinit var toggle:ActionBarDrawerToggle
    lateinit var navController:NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding= ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_login) as NavHostFragment
       // appBarConfiguration = AppBarConfiguration(navController.graph)
       // setupActionBarWithNavController(navController, appBarConfiguration)


        //add toolbar as actionbar
        setSupportActionBar(binding.toolBar)

         navController = navHostFragment.navController

        setupActionBarWithNavController(navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navigationDrawer, navController)
        appBarconfig = AppBarConfiguration(navController.graph, binding.drawerLayout)
        checkingDrawerState(navController, binding.drawerLayout)


        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        binding.navigationDrawer.setupWithNavController(navController)
        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        toggle = ActionBarDrawerToggle(this, binding?.drawerLayout, R.string.nav_open, R.string.nav_close);
        binding.drawerLayout.addDrawerListener(toggle)
        binding.navigationDrawer.bringToFront()
        toggle.syncState()

        writeNewUser("1","name","email")
    }
    fun checkingDrawerState(navController: NavController, drawerLayout: DrawerLayout)
    {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.SecondFragment || destination.id == R.id.providerFragment)
            {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                supportActionBar?.show()
               binding.toolBar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

            }else
            {
                supportActionBar?.hide()
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

        }
    }

    fun writeNewUser(userId: String, name: String, email: String) {
      //  val user = User(name, email)

     //   database.child("users").child(userId).setValue(user)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout?.closeDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.nav_account -> {

                val bundle = Bundle()
                bundle.putBoolean("edit",true)
               navController.navigate(R.id.action_SecondFragment_to_ProfileFragment,bundle)
            }
            R.id.nav_settings -> {

            }
            R.id.nav_logout -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        finish()
                    }
            }
            com.google.android.material.R.id.home->{

            }

        }
        return true
    }


}