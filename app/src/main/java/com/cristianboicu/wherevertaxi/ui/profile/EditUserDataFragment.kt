package com.cristianboicu.wherevertaxi.ui.profile

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
import com.cristianboicu.wherevertaxi.databinding.FragmentEditUserDataBinding
import com.cristianboicu.wherevertaxi.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditUserDataFragment : Fragment() {

    lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding: FragmentEditUserDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_user_data, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.navigateToProfile.observe(viewLifecycleOwner, EventObserver {
            navigateBackProfile()
        })
        viewModel.showToastError.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
        })

        return binding.root
    }


    private fun navigateBackProfile() {
        this.findNavController().navigate(
            EditUserDataFragmentDirections.actionEditUserDataFragmentToProfileFragment()
        )
    }
}