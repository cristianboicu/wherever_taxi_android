package com.cristianboicu.wherevertaxi

import android.os.Bundle
import android.util.Log
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), LogInFragment.SendVerificationCode {

    private lateinit var verificationId: String
    private lateinit var firebaseAuth: FirebaseAuth

    private val navController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()
//        val firebaseAuthSettings = firebaseAuth.firebaseAuthSettings
        Log.d("Logged", firebaseAuth.currentUser.toString())
// Configure faking the auto-retrieval with the whitelisted numbers.
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+37368026688", "123144")
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
            super.onBackPressed()
        }
    }

    override fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
            .build()
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

                LogInFragment.getInstance().navigateVerificationCode()
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                val code = phoneAuthCredential.smsCode
                Toast.makeText(this@MainActivity, code, Toast.LENGTH_LONG).show()

                if (code != null) {
                    // if the code is not null then
                    // we are setting that code to
                    // our OTP edittext field.

                    VerificationCodeFragment.getInstance().setCode(code)

                    // after setting this code
                    // to OTP edittext field we
                    // are calling our verifycode method.

                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    // below method is use to verify code from Firebase.
    private fun verifyCode(code: String) {
        // below line is used for getting
        // credentials from our verification id and code.
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    VerificationCodeFragment.getInstance().navigateHome()
                    Toast.makeText(this@MainActivity," e.message", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

}