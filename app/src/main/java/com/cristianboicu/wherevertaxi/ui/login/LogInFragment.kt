package com.cristianboicu.wherevertaxi.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentLogInBinding
import com.cristianboicu.wherevertaxi.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : Fragment() {

    lateinit var viewModel: LoginViewModel
    lateinit var phnb: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentLogInBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_log_in, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

        setUpUi(binding)
        setUpObservers()
        return binding.root
    }

    private fun setUpObservers() {
        viewModel.verificationCodeSent.observe(viewLifecycleOwner, EventObserver{
            navigateVerificationCode(phnb, it)
        })
        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setUpUi(binding: FragmentLogInBinding) {
        binding.btnSignInGoogle.tvSignInWith.text = this.getString(R.string.sign_in_with_google)

        binding.btnSignIn.setOnClickListener {
            binding.editTextPhone.setText("+37368026689")
            binding.editTextPhone.text?.let { phoneNumber ->
                if (phoneNumber.isNotEmpty() && phoneNumber.isNotBlank()) {
                    phnb = phoneNumber.toString()
                    viewModel.sendVerificationCode(requireActivity(), phnb)
                } else {
                    Toast.makeText(context, "Provide a phone number", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun navigateVerificationCode(phone: String, verificationId: String) {
        this.findNavController().navigate(
            LogInFragmentDirections.actionLogInFragmentToVerificationCodeFragment(phone,
                verificationId)
        )
    }
}

