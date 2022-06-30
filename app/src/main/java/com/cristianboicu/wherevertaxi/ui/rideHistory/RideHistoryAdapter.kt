package com.cristianboicu.wherevertaxi.ui.rideHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cristianboicu.wherevertaxi.data.model.ride.CompletedRide
import com.cristianboicu.wherevertaxi.databinding.ItemRideHistoryBinding

class RideHistoryAdapter :
    ListAdapter<CompletedRide, RideHistoryViewHolder>(RideHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHistoryViewHolder {
        return RideHistoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RideHistoryViewHolder, position: Int) {
        val ride = getItem(position)
        holder.bind(ride)
    }
}

class RideHistoryViewHolder private constructor(private val binding: ItemRideHistoryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: CompletedRide,
    ) {
        binding.ride = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): RideHistoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRideHistoryBinding.inflate(layoutInflater, parent, false)
            return RideHistoryViewHolder(binding)
        }
    }
}

class RideHistoryDiffCallback : DiffUtil.ItemCallback<CompletedRide>() {
    override fun areItemsTheSame(
        oldItem: CompletedRide,
        newItem: CompletedRide,
    ): Boolean {
        return oldItem.rideId == newItem.rideId
    }

    override fun areContentsTheSame(
        oldItem: CompletedRide,
        newItem: CompletedRide,
    ): Boolean {
        return oldItem.rideId == newItem.rideId
    }
}