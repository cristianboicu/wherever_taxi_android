package com.cristianboicu.wherevertaxi

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.cristianboicu.wherevertaxi.ui.login.LogInFragment
import com.cristianboicu.wherevertaxi.ui.verificationCode.VerificationCodeFragment
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.DATABASE_URL
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LogInFragment.SendVerificationCode {

    private lateinit var verificationId: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var phnb: String
    private val navController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTransparentStatusBar()
        setupDrawerLayout()
        firebaseAuth = FirebaseAuth.getInstance()
        database =
            Firebase.database(DATABASE_URL).reference

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

    override fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack)
            .build()
        phnb = phoneNumber
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken,
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s

                LogInFragment.getInstance().navigateVerificationCode(phnb, verificationId)
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                val code = phoneAuthCredential.smsCode
                Toast.makeText(this@MainActivity, code, Toast.LENGTH_LONG).show()

                if (code != null) {
                    VerificationCodeFragment.getInstance().setCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    private fun setTransparentStatusBar() {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
    }

}
