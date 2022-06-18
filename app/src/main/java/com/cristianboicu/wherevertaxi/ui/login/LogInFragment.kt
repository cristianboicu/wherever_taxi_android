package com.cristianboicu.wherevertaxi.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentLogInBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit


class LogInFragment : Fragment() {

    private val TAG = "LogInFragment"
    private lateinit var verificationId: String
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentLogInBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_log_in, container, false)
        binding.lifecycleOwner = this

        mAuth = FirebaseAuth.getInstance()

        setUpUi(binding)
        return binding.root
    }

    private fun setUpUi(binding: FragmentLogInBinding) {
        binding.btnSignInGoogle.tvSignInWith.text = this.getString(R.string.sign_in_with_google)
        binding.btnSignInFacebook.tvSignInWith.text = this.getString(R.string.sign_in_with_facebook)

        binding.btnSignIn.setOnClickListener {
            binding.editTextPhone.text?.let { phoneNumber ->
                if (phoneNumber.isNotEmpty() && phoneNumber.isNotBlank()) {
                    sendVerificationCode(phoneNumber.toString())
                } else {
                    Toast.makeText(context, "Provide a phone number", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                val code = phoneAuthCredential.smsCode

                if (code != null) {
                    // if the code is not null then
                    // we are setting that code to
                    // our OTP edittext field.
//                    edtOTP.setText(code)

                    // after setting this code
                    // to OTP edittext field we
                    // are calling our verifycode method.
                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
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
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    this.findNavController().navigate(
                        LogInFragmentDirections.actionLogInFragmentToHomeFragment()
                    )
                } else {
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
}

