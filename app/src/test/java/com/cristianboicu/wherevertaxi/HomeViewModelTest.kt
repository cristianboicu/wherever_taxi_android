package com.cristianboicu.wherevertaxi

import MainCoroutineRule
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.ui.home.HomeViewModel
import com.cristianboicu.wherevertaxi.ui.home.RideState
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel

    private val uid = "uid1"
    private val rideId = "rid1"
    private val origin = LatLng(14.0, 25.0)
    private val originId = "1"
    private val originAddress = "adresa pornire"
    private val destination = LatLng(11.0, 23.0)
    private val destinationId = "2"
    private val destinationAddress = "adresa destinatie"

    @Before
    fun setUp() = runBlocking {
        val mockRepository = mock(IRepository::class.java)
        val databaseReference = mock(DatabaseReference::class.java)

        `when`(mockRepository.getGeocoding(originId)).thenReturn(origin)
        `when`(mockRepository.getGeocoding(destinationId)).thenReturn(destination)

        `when`(mockRepository.getAuthenticatedUserId()).thenReturn(uid)

        `when`(mockRepository.getReverseGeocoding(origin)).thenReturn(originAddress)
        `when`(mockRepository.getReverseGeocoding(destination)).thenReturn(destinationAddress)

        `when`(mockRepository.postRideRequest(any())).thenReturn(rideId)

        `when`(mockRepository.listenAvailableDrivers()).thenReturn(databaseReference)
        `when`(mockRepository.listenToCompletedRide(any())).thenReturn(databaseReference)
        `when`(mockRepository.listenToRequestedRide(any())).thenReturn(databaseReference)

        viewModel = HomeViewModel(mockRepository, context)
    }

    @Test
    fun `initial state of ride is SELECT_DESTINATION`() {
        assertEquals(RideState.SELECT_DESTINATION, viewModel.rideState.value)
    }

    @Test
    fun `state of ride is SELECT_CAR after user selected route`() = runTest {
        viewModel.onRouteSelected(originId, destinationId)
        assertEquals(RideState.SELECT_CAR, viewModel.rideState.value)
    }

    @Test
    fun `assert origin and destination location are set accordingly`() = runTest {
        viewModel.onRouteSelected(originId, destinationId)
        assertSame(origin, viewModel.origin.value)
        assertSame(destination, viewModel.destination.value)
    }

    @Test
    fun `state of ride is RIDE_PENDING after user selected car`() = runTest {
        viewModel.onRouteSelected(originId, destinationId)
        viewModel.onVehicleClassSelected("vehicleClass")
        assertEquals(RideState.RIDE_PENDING, viewModel.rideState.value)
    }

    @Test
    fun `state of ride is RIDE_ACCEPTED after driver accepted ride`() = runTest {
        viewModel.onRouteSelected(originId, destinationId)
        viewModel.onVehicleClassSelected("")
        viewModel.onRideAccepted(rideId)
        assertEquals(RideState.RIDE_ACCEPTED, viewModel.rideState.value)
    }

    @Test
    fun `state of ride is SELECT_DESTINATION after user cancels the ride`() = runTest {
        viewModel.onRouteSelected(originId, destinationId)
        viewModel.onVehicleClassSelected("")
        viewModel.onRideAccepted(rideId)
        viewModel.onRideCancel(rideId)
        assertEquals(RideState.SELECT_DESTINATION, viewModel.rideState.value)
    }

    @Test
    fun `state of ride is RIDE_COMPLETED after driver finishes ride`() = runTest {
        viewModel.onRouteSelected(originId, destinationId)
        viewModel.onVehicleClassSelected("")
        viewModel.onRideAccepted(rideId)
        viewModel.onRideCompleted(rideId)
        assertEquals(RideState.RIDE_COMPLETED, viewModel.rideState.value)
    }
}