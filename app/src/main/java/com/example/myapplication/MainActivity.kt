package com.example.myapplication

import android.media.MediaPlayer
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
import com.google.android.material.navigation.NavigationView

const val ELEVEN_LABS_API = BuildConfig.ELEVEN_LABS_API


class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfig: AppBarConfiguration

    public var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        Log.d("mainactivity", "created()")


        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


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