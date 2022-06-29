package com.cristianboicu.wherevertaxi.ui.verificationCode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.data.model.user.User
import com.cristianboicu.wherevertaxi.databinding.FragmentVerificationCodeBinding
import com.cristianboicu.wherevertaxi.ui.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationCodeFragment : Fragment() {

    companion object {
        private lateinit var instance: VerificationCodeFragment

        fun getInstance(): VerificationCodeFragment {
            return instance
        }
    }

    lateinit var viewModel: LoginViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var verificationId: String
    private lateinit var phoneNumber: String

    private lateinit var binding: FragmentVerificationCodeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_verification_code, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        firebaseAuth = FirebaseAuth.getInstance()
        instance = this
        phoneNumber = VerificationCodeFragmentArgs.fromBundle(requireArguments()).phoneNumber
        verificationId = VerificationCodeFragmentArgs.fromBundle(requireArguments()).verificationId

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.ivBackVerificationCode.setOnClickListener {
            findNavController().navigate(
                VerificationCodeFragmentDirections.actionVerificationCodeFragmentToLogInFragment()
            )
        }

        binding.btnValidateCode.setOnClickListener {
            verifyCode(binding.etVerificationCode.text.toString())
        }

        return binding.root
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserToDb(firebaseAuth.currentUser!!.uid, User(phone = phoneNumber))
                    navigateHome()
                    Toast.makeText(this.context, "success", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this.context, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateHome() {
        findNavController().navigate(
            VerificationCodeFragmentDirections.actionVerificationCodeFragmentToHomeFragment()
        )
    }

    private fun saveUserToDb(uid: String, user: User) {
        viewModel.saveIfNewUser(uid, user)
    }

    fun setCode(code: String) {
        binding.etVerificationCode.setText(code)
        verifyCode(code)
    }
}