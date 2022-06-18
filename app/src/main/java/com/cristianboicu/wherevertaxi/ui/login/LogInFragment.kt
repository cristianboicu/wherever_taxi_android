package com.cristianboicu.wherevertaxi.ui.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentLogInBinding


class LogInFragment : Fragment() {

    var activityCallback: SendVerificationCode? = null

    public interface SendVerificationCode {
        public fun sendVerificationCode(phoneNumber: String)
    }

    private val TAG = "LogInFragment"

    companion object {
        private lateinit var instance: LogInFragment
        fun getInstance(): LogInFragment {
            return instance
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            activityCallback = activity as SendVerificationCode
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString()
                    + " must implement TextClicked")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentLogInBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_log_in, container, false)
        binding.lifecycleOwner = this
        instance = this

        setUpUi(binding)
        return binding.root
    }

    private fun setUpUi(binding: FragmentLogInBinding) {
        binding.btnSignInGoogle.tvSignInWith.text = this.getString(R.string.sign_in_with_google)
        binding.btnSignInFacebook.tvSignInWith.text = this.getString(R.string.sign_in_with_facebook)

        binding.btnSignIn.setOnClickListener {
            binding.editTextPhone.setText("+37368026689")
            binding.editTextPhone.text?.let { phoneNumber ->
                if (phoneNumber.isNotEmpty() && phoneNumber.isNotBlank()) {
//                    requestVerificationCode("+373 680 26 689")
                    requestVerificationCode(phoneNumber.toString())
                } else {
                    Toast.makeText(context, "Provide a phone number", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestVerificationCode(number: String) {
        activityCallback?.sendVerificationCode(number)
    }

    fun navigateHome() {
        this.findNavController().navigate(
            LogInFragmentDirections.actionLogInFragmentToHomeFragment()
        )
    }
    fun navigateVerificationCode() {
        Log.d("Aloha", "epta")

        this.findNavController().navigate(
            LogInFragmentDirections.actionLogInFragmentToVerificationCodeFragment()
        )
    }
}

