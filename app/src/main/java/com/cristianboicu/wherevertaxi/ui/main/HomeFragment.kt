package com.cristianboicu.wherevertaxi.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.databinding.FragmentHomeBinding
import com.cristianboicu.wherevertaxi.ui.adapter.places.PlacesAdapter
import com.cristianboicu.wherevertaxi.ui.adapter.places.PlacesListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var map: GoogleMap
    private lateinit var drawer: DrawerLayout
    private val TAG = "HomeFragment"
    private lateinit var mBottomSheetLayout: ConstraintLayout
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var viewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mCurrentLocation: Location
    private lateinit var adapter: PlacesAdapter

    private var activityResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { result ->
            var allAreGranted = true
            for (b in result.values) {
                allAreGranted = allAreGranted && b
            }

            if (allAreGranted) {
                enableMyLocation()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding: FragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setUpUi(binding, savedInstanceState)

        binding.viewModel = viewModel
        binding.bottomSheet.viewModel = viewModel

        setUpObserver()

        adapter = PlacesAdapter(PlacesListener {
            viewModel.onPlaceSelected(it)
        })

        binding.bottomSheet.rvAutocomplete.adapter = adapter

        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun setUpObserver() {
        viewModel.drawMarkers.observe(viewLifecycleOwner) {
            for (marker in it) {
                map.addMarker(marker)
            }
            if (it.isNotEmpty()) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(it[it.size - 1].position, 11.6f))
            }
        }

        viewModel.drawPolyLine.observe(viewLifecycleOwner) {
            map.clear()
            map.addPolyline(it)
        }

        viewModel.placesPredictions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            for (item in it) {
                Log.d("Prediction: ", item.placeId)
            }
        }

        viewModel.requestCurrentLocation.observe(viewLifecycleOwner) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        mCurrentLocation = it
                        viewModel.routeSelected(LatLng(it.latitude, it.longitude))
                        Log.d("HomeFragment",
                            "${mCurrentLocation.latitude} , ${mCurrentLocation.longitude}")
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
    }

    private fun setUpUi(binding: FragmentHomeBinding, savedInstanceState: Bundle?) {
        val mMapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)
        mMapFragment?.getMapAsync(this)

        configureShowMyLocationButton(mMapFragment)

        mBottomSheetLayout = binding.bottomSheet.bottomSheetLayout
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout)

        binding.bottomSheet.etWhereTo.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            sheetBehavior.setDraggable(true)
        }
        binding.btnNavigationDrawer.setOnClickListener {
            openDrawer()
        }
    }

    private fun configureShowMyLocationButton(mMapFragment: SupportMapFragment?) {
        val locationButton = (mMapFragment!!.requireView().findViewById<View>("1".toInt())
            .parent as View).findViewById<View>("2".toInt())
        val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 280)
    }

    private fun openDrawer() {
        activity?.let {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            return
        }

        val appPerms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        activityResultLauncher.launch(appPerms)

    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
//        newInstance(true).show(supportFragmentManager, "dialog")
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(context, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }
}
//
//val locationButton =
//    (mapFragment?.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(
//        Integer.parseInt("2")) as ImageView locationButton . setImageResource (R.drawable.ic_my_location_button)