package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myapplication.R.*
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import java.util.*

const val ELEVEN_LABS_API = BuildConfig.ELEVEN_LABS_API


class MainScreen : AppCompatActivity() {


    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var prefs: SharedPreferences
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)



        val lang = prefs.getString(getString(R.string.pref_language_key), getString(R.string.pref_lang_eng))

        listener = SharedPreferences.OnSharedPreferenceChangeListener{sharedPreferences, key ->
            Log.d("keychange,", key)
        }

//        if(lang == "svenska"){
//            val locale = Locale("sv", "SE")
//            Locale.setDefault(locale)
//            val resources = resources
//            val configuration = resources.configuration
//            configuration.setLocale(locale)
////            recreate()
//            val intent = Intent(this, MainScreen::class.java)
//            startActivity(intent)
//        }
//        else if(lang == "English"){
//            val locale = Locale("en", "US")
//            Locale.setDefault(locale)
//            val resources = resources
//            val configuration = resources.configuration
//            configuration.setLocale(locale)
//            recreate()
//        }




        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        Log.d("clonedvoice", getString(R.string.successfully_cloned, "hello"))


        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment
            ) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfig = AppBarConfiguration(navController.graph)

        val drawerLayout: DrawerLayout =
            findViewById(R.id.drawer_layout)
        appBarConfig =
            AppBarConfiguration(navController.graph, drawerLayout)

        findViewById<NavigationView>(R.id.nav_view)
            ?.setupWithNavController(navController)

        setupActionBarWithNavController(
            navController,
            appBarConfig
        )
    }


    override fun onSupportNavigateUp(): Boolean {

        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

}