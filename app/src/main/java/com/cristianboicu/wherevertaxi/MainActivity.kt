package com.cristianboicu.wherevertaxi

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val navController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTransparentStatusBar()
        setupDrawerLayout()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(findViewById<DrawerLayout>(R.id.drawer_layout))
                || super.onSupportNavigateUp()
    }

    private fun setupDrawerLayout() {
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)
    }

    override fun onBackPressed() {
        if (findViewById<DrawerLayout>(R.id.drawer_layout).isDrawerOpen(GravityCompat.START)) {
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    private fun setTransparentStatusBar() {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
    }

}
