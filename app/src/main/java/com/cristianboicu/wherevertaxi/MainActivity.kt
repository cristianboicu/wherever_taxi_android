package com.cristianboicu.wherevertaxi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private val navController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDrawerLayout()
    }

    override fun onSupportNavigateUp(): Boolean {
//        return NavigationUI.navigateUp(navController,
//            findViewById<DrawerLayout>(R.id.drawer_layout))
        return findNavController(R.id.nav_host_fragment).navigateUp(findViewById<DrawerLayout>(R.id.drawer_layout))
                || super.onSupportNavigateUp()
    }

    private fun setupDrawerLayout() {
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)
//        NavigationUI.setupActionBarWithNavController(this,
//            navController,
//            findViewById<DrawerLayout>(R.id.drawer_layout))
    }

    override fun onBackPressed() {
        Log.d("MainActivity", "onBackPressed")
        if (findViewById<DrawerLayout>(R.id.drawer_layout).isDrawerOpen(GravityCompat.START)) {
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
            Log.d("MainActivity", "onBackPressed1")
        } else {
            Log.d("MainActivity", "onBackPressed2")
//            navigateUp(navController, findViewById<DrawerLayout>(R.id.drawer_layout))
            super.onBackPressed()
        }
    }

}