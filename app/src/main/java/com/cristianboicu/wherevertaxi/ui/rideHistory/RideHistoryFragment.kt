package com.cristianboicu.wherevertaxi.ui.rideHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentRideHistoryBinding
import com.cristianboicu.wherevertaxi.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideHistoryFragment : Fragment() {
    private lateinit var adapter: RideHistoryAdapter
    lateinit var viewModel: RideHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentRideHistoryBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_ride_history, container, false)
        viewModel = ViewModelProvider(this)[RideHistoryViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        adapter = RideHistoryAdapter()
        binding.rvRidesHistory.adapter = adapter

        viewModel.completedRides.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            navigateBackHome()
        })

        return binding.root
    }

    private fun navigateBackHome() {
        this.findNavController().navigate(
            RideHistoryFragmentDirections.actionRideHistoryFragmentToHomeFragment()
        )
    }
}