package com.cristianboicu.wherevertaxi.ui.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentPaymentBinding
import com.cristianboicu.wherevertaxi.databinding.ItemCardBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentFragment : Fragment() {
    lateinit var viewModel: PaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentPaymentBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_payment, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[PaymentViewModel::class.java]
        binding.viewModel = viewModel

        setUpObservers(binding, container!!)
        return binding.root
    }

    private fun setUpObservers(binding: FragmentPaymentBinding, container: ViewGroup) {
        viewModel.navigateBackFromPayment.observe(viewLifecycleOwner) {
            this.findNavController().navigate(
                PaymentFragmentDirections.actionPaymentFragmentToHomeFragment()
            )
        }
        viewModel.navigateToAddCard.observe(viewLifecycleOwner) {
            this.findNavController().navigate(
                PaymentFragmentDirections.actionPaymentFragmentToAddPaymentFragment()
            )
        }

        viewModel.paymentMethods.observe(viewLifecycleOwner) {
            Log.d("Payment", it.size.toString())

            val root = binding.layoutPayments

            for (i in it.indices) {
                val element: ItemCardBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_card, container, false)

                element.tvCardNumber.text = it[i].cardNumber
                element.root.id = i
                root.addView(element.root)
            }
            setUpUi(binding)
        }
    }

    private fun setUpUi(binding: FragmentPaymentBinding) {

        binding.layoutMain.getViewsByType(LinearLayout::class.java).apply {
            for (item in this) {
                item.setOnClickListener {
                    Log.d("Payment", item.id.toString())
                    binding.layoutMain.getViewsByType(RadioButton::class.java).apply {
                        for (i in this) {
                            i.isChecked = false
                        }
                    }
                    it.findViewById<RadioButton>(R.id.rb_selected).isChecked = true
                }
            }
        }
    }

    fun <T : View> ViewGroup.getViewsByType(tClass: Class<T>): List<T> {
        return mutableListOf<T?>().apply {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                (child as? ViewGroup)?.let {
                    addAll(child.getViewsByType(tClass))
                }
                if (tClass.isInstance(child))
                    add(tClass.cast(child))
            }
        }.filterNotNull()
    }
}