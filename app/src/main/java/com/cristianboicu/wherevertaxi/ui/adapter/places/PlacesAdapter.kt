package com.cristianboicu.wherevertaxi.ui.adapter.places

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cristianboicu.wherevertaxi.databinding.ItemPlaceBinding
import com.google.android.libraries.places.api.model.AutocompletePrediction

class PlacesAdapter(private val clickListener: PlacesListener) :
    ListAdapter<AutocompletePrediction, PlaceViewHolder>(PlaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val repository = getItem(position)
        holder.bind(repository, clickListener)
    }

}

class PlaceViewHolder private constructor(private val binding: ItemPlaceBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: AutocompletePrediction,
        clickListener: PlacesListener,
    ) {
        binding.placePrimary = item.getPrimaryText(null).toString()
        binding.placeSecondary = item.getSecondaryText(null).toString()
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): PlaceViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaceBinding.inflate(layoutInflater, parent, false)
            return PlaceViewHolder(binding)
        }
    }
}

class PlaceDiffCallback : DiffUtil.ItemCallback<AutocompletePrediction>() {
    override fun areItemsTheSame(
        oldItem: AutocompletePrediction,
        newItem: AutocompletePrediction,
    ): Boolean {
        return oldItem.placeId == newItem.placeId
    }

    override fun areContentsTheSame(
        oldItem: AutocompletePrediction,
        newItem: AutocompletePrediction,
    ): Boolean {
        return oldItem.placeId == newItem.placeId
    }
}

class PlacesListener(val clickListener: (placeId: String) -> Unit) {
    fun onClick(place: AutocompletePrediction) = clickListener(place.placeId)
}