package com.cristianboicu.wherevertaxi.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentAddPaymentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPaymentFragment : Fragment() {
    lateinit var viewModel: PaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentAddPaymentBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_add_payment, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[PaymentViewModel::class.java]
        binding.viewModel = viewModel

        setUpObservers(binding)
        return binding.root
    }

    private fun setUpObservers(binding: FragmentAddPaymentBinding) {
        viewModel.navigateBackFromAddCard.observe(viewLifecycleOwner) {
            this.findNavController().navigate(
                AddPaymentFragmentDirections.actionAddPaymentFragmentToPaymentFragment()
            )
        }
    }

}