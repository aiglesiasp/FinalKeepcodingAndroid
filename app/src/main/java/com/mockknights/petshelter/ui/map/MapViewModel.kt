package com.mockknights.petshelter.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.mockknights.petshelter.R
import com.mockknights.petshelter.data.remote.response.Address
import com.mockknights.petshelter.domain.PetShelter
import com.mockknights.petshelter.domain.Repository
import com.mockknights.petshelter.domain.ShelterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.lang.StrictMath.pow
import java.lang.StrictMath.toRadians
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class MapViewModel @Inject constructor(private val repository: Repository, private val coroutineDispatcher: CoroutineDispatcher): ViewModel() {

    private val _mapShelterListState = MutableStateFlow<MapShelterListState>(MapShelterListState.Loading)
    val mapShelterListState: MutableStateFlow<MapShelterListState> get() = _mapShelterListState

    private val _modalShelterList = MutableStateFlow(emptyList<PetShelter>())
    val modalShelterList: MutableStateFlow<List<PetShelter>> get() = _modalShelterList

    private val _sheetState = MutableStateFlow(BottomSheetState(initialValue = BottomSheetValue.Collapsed))
    val bottomSheetScaffoldState = BottomSheetScaffoldState(
        bottomSheetState = _sheetState.value,
        drawerState = DrawerState(DrawerValue.Closed),
        snackbarHostState = SnackbarHostState()
    )

    private val _locationPermissionGranted = mutableStateOf(false)
    val locationPermissionGranted: MutableState<Boolean> get() = _locationPermissionGranted

    private val _currentUserLocation = mutableStateOf(LatLng(40.4167047, -3.7035825)) // Madrid by default
    private val currentUserLocation: MutableState<LatLng> get() = _currentUserLocation

    private val _cameraPositionState = MutableStateFlow(CameraPositionState(CameraPosition.fromLatLngZoom(currentUserLocation.value, 6f)))
    val cameraPositionState: MutableStateFlow<CameraPositionState> get() = _cameraPositionState

    private fun setValueOnMainThreadShelter(value: MapShelterListState) {
        viewModelScope.launch(Dispatchers.Main) {
            _mapShelterListState.value = value
        }
    }

    init {
        getPetShelters()
    }

    private fun getPetShelters() {
        viewModelScope.launch(coroutineDispatcher) {
            try {
                val petShelters = repository.getAllPetShelter().flowOn(coroutineDispatcher)
                if(petShelters.first().isEmpty()) throw Exception("Empty pet shelter list")
                setValueOnMainThreadShelter(MapShelterListState.Success(petShelters.first()))
            } catch (e: Exception) {
                setValueOnMainThreadShelter(MapShelterListState.Error(e.message.toString()))
            }
        }
    }

    fun setModalShelter(shelterName: String) {
        val petShelters = (_mapShelterListState.value as? MapShelterListState.Success)?.petShelters ?: listOf()
        val modalPetShelter = petShelters.filter { it.name == shelterName }
        viewModelScope.launch(Dispatchers.Main) {
            _modalShelterList.value = modalPetShelter
        }
    }

    fun toggleModal(coroutineScope: CoroutineScope) {
        // Needed to use composable functions
        coroutineScope.launch {
            when (_sheetState.value.isCollapsed) {
                true -> _sheetState.value.expand()
                false -> _sheetState.value.collapse()
            }
        }
    }

    fun collapseModal(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            _sheetState.value.collapse()
        }
    }

    fun getShelterIconByShelterType(shelterType: String): Int {
        return when (shelterType) {
            ShelterType.PARTICULAR.stringValue -> R.drawable.particular
            ShelterType.LOCAL_GOVERNMENT.stringValue -> R.drawable.towncouncil
            ShelterType.VETERINARY.stringValue -> R.drawable.veterinary
            ShelterType.SHELTER_POINT.stringValue -> R.drawable.animalshelter
            ShelterType.KIWOKO_STORE.stringValue -> R.drawable.kiwoko
            else -> R.drawable.questionmark
        }
    }

    fun onPermissionRequestCompleted(isGranted: Boolean, context: Context) {
        // Change the status of the permission
        _locationPermissionGranted.value = isGranted
        // If the permission is granted, get the user location
        if (isGranted) { setCurrentUserLocation(context) }
    }

    @SuppressLint("MissingPermission") // Permission is checked with locationPermissionState
    private fun setCurrentUserLocation(context: Context) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if (locationPermissionGranted.value) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let {nullCheckedLocation ->
                    _currentUserLocation.value = LatLng(nullCheckedLocation.latitude, nullCheckedLocation.longitude)
                    _cameraPositionState.value = CameraPositionState(CameraPosition.fromLatLngZoom(currentUserLocation.value, 6f))
                }
            }
        }
    }

    fun moveCameraToUserLocation(coroutineScope: CoroutineScope) {
        val update = CameraUpdateFactory.newCameraPosition(CameraPosition(currentUserLocation.value, 9f, 0f, 0f))
        coroutineScope.launch {
            _cameraPositionState.value.animate(update)
        }
    }

    private fun moveCameraToLocation(coroutineScope: CoroutineScope, location: LatLng) {
        val update = CameraUpdateFactory.newCameraPosition(CameraPosition(location, 9f, 0f, 0f))
        coroutineScope.launch {
            _cameraPositionState.value.animate(update)
        }
    }

    fun onClosestShelterClicked(coroutineScope: CoroutineScope) {
        // If there is no shelter, do nothing
        if ((_mapShelterListState.value as? MapShelterListState.Success)?.petShelters.isNullOrEmpty()) return
        // Get closest shelter and move camera to it
        val closestShelter = getClosestShelter()
        closestShelter?.let { unwrappedClosestShelter ->
            modalShelterList.value = listOf(unwrappedClosestShelter)
            toggleModal(coroutineScope)
            moveCameraToLocation(
                coroutineScope = coroutineScope,
                location = LatLng(unwrappedClosestShelter.address.latitude, unwrappedClosestShelter.address.longitude)
            )
        }
    }

    private fun getClosestShelter(): PetShelter? {
        // If there is no shelter, return an empty shelter
        if ((_mapShelterListState.value as? MapShelterListState.Success)?.petShelters.isNullOrEmpty()) return null
        val petShelters = (_mapShelterListState.value as MapShelterListState.Success).petShelters // Checked cast
        // When permission is granted, the user location is the origin
        val origin: LatLng? = if (locationPermissionGranted.value) currentUserLocation.value else null
        // If there is an origin, get the closest shelter
        var closestShelter: PetShelter? = null
        origin?.let {unwrappedOrigin ->
            closestShelter = petShelters
                .filter { petShelter ->
                    petShelter.address.latitude - unwrappedOrigin.latitude <= 1 &&
                            petShelter.address.longitude - unwrappedOrigin.longitude <= 1
                }
                .sortedWith { shelter1, shelter2 ->
                    val coordinatesShelter1 =
                        LatLng(shelter1.address.latitude, shelter1.address.longitude)
                    val coordinatesShelter2 =
                        LatLng(shelter2.address.latitude, shelter2.address.longitude)
                    when {
                        distanceBetween(unwrappedOrigin, coordinatesShelter1) == distanceBetween(
                            unwrappedOrigin,
                            coordinatesShelter2
                        ) -> 0
                        distanceBetween(unwrappedOrigin, coordinatesShelter1) < distanceBetween(
                            unwrappedOrigin,
                            coordinatesShelter2
                        ) -> -1
                        else -> 1
                    }
                }
                .firstOrNull()
        }
        return closestShelter
    }

    private fun distanceBetween(origin: LatLng, destination: LatLng): Double {
        val earthRadius = 6371 // Radius of the earth in km
        val dLat = Math.toRadians(destination.latitude - origin.latitude)  // Increment of latitude
        val dLon = Math.toRadians(destination.longitude - origin.longitude) // Increment of longitude
        // Haversine formula
        val a = pow(sin(dLat / 2), 2.0) +
                cos(toRadians(origin.latitude)) * cos(Math.toRadians(destination.latitude)) *
                pow(sin(dLon / 2), 2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun onCall(phone: String, localContext: Context) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        localContext.startActivity(intent)
    }

    fun onGo(address: Address, localContext: Context) {
        val gmmIntentUri = Uri.parse("google.navigation:q=${address.latitude},${address.longitude}&mode=w") // w = walking
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.resolveActivity(localContext.packageManager)?.let {
            localContext.startActivity(mapIntent)
        }
    }
}