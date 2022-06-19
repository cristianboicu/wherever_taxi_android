package com.cristianboicu.wherevertaxi.ui.profile

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ProfileFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentProfileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setUpUi(binding)
        return binding.root
    }

    private fun setUpUi(binding: FragmentProfileBinding) {
        binding.imageButton.setOnClickListener {
            navigateBackHome()
        }
        binding.tvLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            restartApp()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun restartApp() {
        val activity = requireActivity()

        val am = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am[AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + 500] =
            PendingIntent.getActivity(activity, 0, activity.intent, PendingIntent.FLAG_ONE_SHOT
                    or PendingIntent.FLAG_CANCEL_CURRENT)
        val i = activity.baseContext.packageManager
            .getLaunchIntentForPackage(activity.baseContext.packageName)

        i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    private fun navigateBackHome() {
        this.findNavController().navigate(
            ProfileFragmentDirections.actionProfileFragmentToHomeFragment()
        )
    }
}