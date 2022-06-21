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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentProfileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding.viewModel = viewModel

        setUpUi(binding)

        viewModel.loadedUser.observe(viewLifecycleOwner) {
            it.let {
                binding.user = it
            }
        }

        return binding.root
    }

    private fun setUpUi(binding: FragmentProfileBinding) {
        binding.imageButton.setOnClickListener {
            navigateBackHome()
        }
        binding.profileLogOut.setOnClickListener {
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