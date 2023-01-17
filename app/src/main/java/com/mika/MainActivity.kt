package com.mika

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.LayoutDirection
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
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
import androidx.preference.PreferenceManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mika.R.*
import com.mika.databinding.ActivityMainBinding
import com.mika.utils.changeLang
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var appBarconfig: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var navController: NavController

    val mainViewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(id.nav_host_fragment_content_login) as NavHostFragment
        // appBarConfiguration = AppBarConfiguration(navController.graph)
        // setupActionBarWithNavController(navController, appBarConfiguration)

        //add toolbar as actionbar
        setSupportActionBar(binding.toolBar)
        checkLang()
        initObservers()

        navController = navHostFragment.navController

        setupActionBarWithNavController(navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navigationDrawer, navController)
        appBarconfig = AppBarConfiguration(navController.graph, binding.drawerLayout)
        checkingDrawerState(navController, binding.drawerLayout)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        binding.navigationDrawer.setupWithNavController(navController)
        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            string.nav_open,
            string.nav_close
        );
        binding.drawerLayout.addDrawerListener(toggle)
        binding.navigationDrawer.bringToFront()
        toggle.syncState()

//        writeNewUser("1","name","email")
    }

    fun checkLang() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val lan = pref.getString("language", "Arabic")
        Log.d("getLocale", " $lan")
        if(lan == "Arabic")
            changeLang(this,"ar")
        binding.root.layoutDirection = View.LAYOUT_DIRECTION_LOCALE
//        if (lan == "Arabic")
//            binding.root.layoutDirection = View.LAYOUT_DIRECTION_RTL
//        else
//            binding.root.layoutDirection = View.LAYOUT_DIRECTION_LTR

    }

    private fun checkingDrawerState(navController: NavController, drawerLayout: DrawerLayout) {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == id.SecondFragment || destination.id == id.providerFragment) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                supportActionBar?.show()

                binding.toolBar.setBackgroundColor(getResources().getColor(color.orange, null))
                binding.toolBar.setNavigationIcon(drawable.ic_baseline_menu_24)
                Log.d("alsdksaldkasdl", " Fragment ${destination.id}")

            } else {
                supportActionBar?.hide()
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                Log.d("alsdksaldkasdl", " else ${destination.id}")
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            id.nav_account -> {
                val bundle = Bundle()
                bundle.putBoolean("edit", true)
                navController.navigate(id.action_SecondFragment_to_FirstFragment, bundle)
            }
            id.nav_settings -> {
                navController.navigate(id.settingsFragment)
            }
            id.nav_logout -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                       val intent =Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
            }
            com.google.android.material.R.id.home -> {

            }

        }
        return true
    }

    fun initObservers(){
        mainViewModel.languageLiveDate.observe(this){
            Log.d("asdlkasdklasld", " ${it}")
            if(it!= null && it == "ar")
                changeLang(this,"ar")
            else if (it != null && it == "en")
                changeLang(this,"en")
            binding.root.layoutDirection = View.LAYOUT_DIRECTION_LOCALE
        }
    }
}