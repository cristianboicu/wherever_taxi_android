package com.cristianboicu.wherevertaxi.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentLogInBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_log_in, container, false)
        binding.lifecycleOwner = this

        setUpUi(binding)
        return binding.root
    }

    private fun setUpUi(binding: FragmentLogInBinding) {
        binding.btnSignInGoogle.tvSignInWith.text = this.getString(R.string.sign_in_with_google)
        binding.btnSignInFacebook.tvSignInWith.text = this.getString(R.string.sign_in_with_facebook)

        binding.btnSignIn.setOnClickListener {
            this.findNavController().navigate(
                LogInFragmentDirections.actionLogInFragmentToHomeFragment()
            )
        }
    }

}