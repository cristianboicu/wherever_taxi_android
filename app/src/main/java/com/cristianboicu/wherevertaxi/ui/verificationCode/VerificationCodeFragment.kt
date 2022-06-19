package com.cristianboicu.wherevertaxi.ui.verificationCode

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentVerificationCodeBinding

class VerificationCodeFragment : Fragment() {

    companion object {
        private lateinit var instance: VerificationCodeFragment

        fun getInstance(): VerificationCodeFragment {
            return instance
        }
    }

    private lateinit var binding: FragmentVerificationCodeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_verification_code, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        instance = this
        return binding.root
    }

    fun navigateHome() {
        findNavController().navigate(
            VerificationCodeFragmentDirections.actionVerificationCodeFragmentToHomeFragment()
        )
    }

    fun setCode(code: String){
        binding.etVerificationCode.setText(code)
    }


}