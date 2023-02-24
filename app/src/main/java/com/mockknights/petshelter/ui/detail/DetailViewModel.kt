package com.mockknights.petshelter.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mockknights.petshelter.data.remote.response.Address
import com.mockknights.petshelter.domain.PetShelter
import com.mockknights.petshelter.domain.Repository
import com.mockknights.petshelter.domain.ShelterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel@Inject constructor(private val repository: Repository): ViewModel() {

    private val _detailState = MutableStateFlow(PetShelter("", "", "","", Address(0.0, 0.0), ShelterType.PARTICULAR, ""))
    val detailState: MutableStateFlow<PetShelter> get() = _detailState

    fun getShelterDetail(id: String) {
        viewModelScope.launch {
            val result = repository.getShelter(id).flowOn(Dispatchers.IO)
            _detailState.value = result.first()
        }
    }

    fun onUpdatedShelterType(shelterType: ShelterType) {
        updateShelterType(shelterType)
    }
    private fun updateShelterType(shelterType: ShelterType) {
        val newDetailState = _detailState.value.copy(shelterType = shelterType)
        viewModelScope.launch (Dispatchers.IO) {
            _detailState.value = newDetailState
        }
    }

    fun onUpdatedPhone(phone: String) {
        updatePhone(phone)
    }
    private fun updatePhone(phone: String) {
        val newDetailState = _detailState.value.copy(phoneNumber = phone)
        viewModelScope.launch (Dispatchers.IO) {
            _detailState.value = newDetailState
        }
    }

    fun onUpdatedAddress(latitude: String, longitude: String) {
        updateAddress(latitude, longitude)
    }
    private fun updateAddress(latitude: String, longitude: String) {
        val newDetailState = _detailState.value.copy(address = Address(latitude.toDouble(), longitude.toDouble()))
        viewModelScope.launch (Dispatchers.IO) {
            _detailState.value = newDetailState
        }
    }

    fun onEditName(name: String) {
        updateUserName(name)
    }
    private fun updateUserName(name: String) {
        val newDetailState = _detailState.value.copy(name = name)
        viewModelScope.launch (Dispatchers.IO) {
            _detailState.value = newDetailState
        }
    }
}


