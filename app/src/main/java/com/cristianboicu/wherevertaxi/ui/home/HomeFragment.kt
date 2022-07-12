package com.cristianboicu.wherevertaxi.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
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
import com.cristianboicu.wherevertaxi.utils.CustomAnimator
import com.cristianboicu.wherevertaxi.utils.EventObserver
import com.cristianboicu.wherevertaxi.utils.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private var activityResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { result ->
            var allAreGranted = true
            for (b in result.values) {
                allAreGranted = allAreGranted && b
            }

            if (allAreGranted) {
                enableUserCurrentLocation()
            }
        }

    private lateinit var map: GoogleMap
    private lateinit var drawer: DrawerLayout
    private lateinit var mBottomSheetLayout: ConstraintLayout
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var viewModel: HomeViewModel
    private lateinit var adapter: PlacesAdapter
    private lateinit var binding: FragmentHomeBinding

    private var mCurrentLocation: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val listOfDrivers = mutableListOf<Marker>()
    private var clientTripPath: Polyline? = null
    private var clientTripDestinationMarker: Marker? = null
    private var driverMarker: Marker? = null
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())
        viewModel = ViewModelProvider(this.requireActivity())[HomeViewModel::class.java]

        binding.viewModel = viewModel
        binding.bottomSheet.viewModel = viewModel

        adapter = PlacesAdapter(PlacesListener { placeId: String, placePrimary: String ->
            onPlaceClicked(placeId,
                placePrimary)
        })

        binding.bottomSheet.rvAutocomplete.adapter = adapter
        setUpUi(binding, savedInstanceState)

        requestCurrentLocation()

        return binding.root
    }

    private fun onPlaceClicked(placeId: String, placePrimary: String) {
        viewModel.onPlaceSelected(
            placeId = placeId,
            placePrimary = placePrimary,
            binding.bottomSheet.etPickupPoint,
            binding.bottomSheet.etWhereTo
        )
    }

    private fun setUpObserver() {

        viewModel.driverLocation.observe(viewLifecycleOwner, EventObserver { it ->
            removeAllDriverMarkers()
            drawCurrentDriverMarker(it)
            CustomAnimator.animateMarkerToICS(driverMarker, it)
        })

        viewModel.clientToDestinationPath.observe(viewLifecycleOwner) {
            it?.let {
                removeOldPath()
                drawNewPath(it)
            }
        }
        viewModel.availableDriverMarkers.observe(viewLifecycleOwner) {
            removeAllDriverMarkers()
            listOfDrivers.clear()

            for (marker in it) {
                listOfDrivers.add(map.addMarker(marker)!!)
            }
        }

        viewModel.clearData.observe(viewLifecycleOwner, EventObserver {
            map.clear()
            driverMarker = null
            binding.bottomSheet.etPickupPoint.setText("")
            binding.bottomSheet.etWhereTo.setText("")
        })

        viewModel.placesPredictions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.requestCurrentLocation.observe(viewLifecycleOwner, EventObserver {
            requestCurrentLocation()
        })

        viewModel.userLocationAddressPlain.observe(viewLifecycleOwner) {
            binding.bottomSheet.etPickupPoint.setText(it)
        }
    }

    private fun drawCurrentDriverMarker(position: LatLng) {
        val resizedBitmapIcon: Bitmap = Util.getBitmapFromSvg(context, R.drawable.car_model)!!

        if (driverMarker == null) {
            driverMarker = map.addMarker(
                MarkerOptions()
                    .position(position).icon(resizedBitmapIcon.let {
                        BitmapDescriptorFactory.fromBitmap(it)
                    }))

        }
    }

    private fun removeAllDriverMarkers() {
        for (driver in listOfDrivers) {
            driver.remove()
        }
    }

    private fun drawNewPath(path: String) {
        val decodedShape = PolyUtil.decode(path)
        clientTripPath = map.addPolyline(drawPolyline(decodedShape))
        clientTripDestinationMarker = map.addMarker(
            MarkerOptions()
                .position(decodedShape[decodedShape.size - 1]))
    }

    private fun removeOldPath() {
        clientTripPath?.remove()
        clientTripDestinationMarker?.remove()
    }

    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    viewModel.origin.value = LatLng(it.latitude, it.longitude)
                    mCurrentLocation = LatLng(it.latitude, it.longitude)
                    viewModel.getReverseGeocoding(mCurrentLocation)
//                    Log.d(TAG," ${mCurrentLocation.toString().split(',')[0]}")
//                    binding.bottomSheet.etPickupPoint.setText(mCurrentLocation.toString().split(',')[0])
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude,
                        it.longitude),
                        15f))
                }
            }
    }


    private fun drawPolyline(line: List<LatLng>): PolylineOptions {
        return PolylineOptions()
            .addAll(line)
            .width(8f)
            .color(Color.BLUE)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUi(binding: FragmentHomeBinding, savedInstanceState: Bundle?) {
        setUpMap()

        binding.bottomSheet.standardCar.layoutCarType.isSelected = true
        binding.bottomSheet.comfortCar.layoutCarType.isSelected = false
        binding.bottomSheet.comfortCar.tvCarType.text = "Comfort"
        binding.bottomSheet.comfortCar.tvPrice.text = "LEI 22.3"

        mBottomSheetLayout = binding.bottomSheet.bottomSheetLayout
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout)

        binding.bottomSheet.etWhereTo.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            sheetBehavior.setDraggable(true)
        }

        binding.bottomSheet.etPickupPoint.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            sheetBehavior.setDraggable(true)
        }

        binding.bottomSheet.standardCar.layoutCarType.setOnClickListener {
            it.isSelected = true
            binding.bottomSheet.comfortCar.layoutCarType.isSelected = false
        }

        binding.bottomSheet.comfortCar.layoutCarType.setOnClickListener {
            it.isSelected = true
            binding.bottomSheet.standardCar.layoutCarType.isSelected = false
        }

        binding.bottomSheet.btnSelectCar.setOnClickListener {
            selectVehicleClass(it)
        }

        binding.btnNavigationDrawer.setOnClickListener {
            openLeftMenu()
        }
    }

    private fun selectVehicleClass(it: View?) {
        val comfortCar =
            binding.bottomSheet.comfortCar.layoutCarType.isEnabled && binding.bottomSheet.comfortCar.layoutCarType.isSelected

        var vehicleClass = VehicleClass.STANDARD.toString()
        if (comfortCar) {
            vehicleClass = VehicleClass.COMFORT.toString()
        }
        viewModel.onVehicleClassSelected(vehicleClass)
    }

    private fun setUpMap() {
        val mMapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)
        mMapFragment?.getMapAsync(this)

        configureShowMyLocationButton(mMapFragment)
    }

    private fun configureShowMyLocationButton(mMapFragment: SupportMapFragment?) {
        val locationButton = (mMapFragment!!.requireView().findViewById<View>("1".toInt())
            .parent as View).findViewById<View>("2".toInt())
        val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 760)
    }

    private fun openLeftMenu() {
        activity?.let {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.setOnMyLocationButtonClickListener(this)
        enableUserCurrentLocation()
        setUpObserver()
    }

    @SuppressLint("MissingPermission")
    private fun enableUserCurrentLocation() {
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

    override fun onResume() {
        super.onResume()
        drawer = requireActivity().findViewById(R.id.drawer_layout)
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "fragment destroyed")
    }
}
