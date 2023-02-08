package com.mockknights.petshelter.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.mockknights.petshelter.domain.PetShelter
import com.mockknights.petshelter.ui.components.createButton

@Preview(showSystemUi = true)
@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {

    //PEDIR PERMISOS
    //ActivityCompat.checkSelfPermission(requireContext(), )

    val petShelter = viewModel.petShelter.collectAsState()

    MyGoogleMaps(petShelter.value)
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Bottom) {
        createButton(name = "ME", color = Color.Red) {
            //TODO: Llamar a mi Localizacion
        }
    }
}


@Composable
fun MyGoogleMaps(petShelter: List<PetShelter> = emptyList()) {
    val marker = LatLng(41.5678, -16.8)
    //val teide = LatLng(petShelter[0].address.latitude, petShelter[0].address.longitude)
    //val madrid = LatLng(petShelter[1].address.latitude, petShelter[1].address.longitude)
    //val igualada = LatLng(petShelter[2].address.latitude, petShelter[2].address.longitude)
    //val otra = LatLng(petShelter[3].address.latitude, petShelter[3].address.longitude)

    //PORPIEDAS DE LOS MAPAS
    //1- MODIFICADOR
    val modifier by remember {
        mutableStateOf(Modifier.fillMaxSize())
    }
    //2- POSICION DE LA CAMARA
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(marker, 1f)
    }
   //3- PROPIEDADES DEL MAPA
    val properties by remember {
        mutableStateOf(MapProperties(
            mapType = MapType.NORMAL,
            //isMyLocationEnabled = true,
            //latLngBoundsForCameraTarget = LatLngBounds(marker2, marker3)
        )) }

    //4- UI SETTINGS
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true, tiltGesturesEnabled = true))}

    GoogleMap(modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapClick = { },
        onMapLoaded = { },
        onMyLocationButtonClick = { true },
        onMyLocationClick = { },
        onPOIClick = { }
    ) {

        Marker(
            MarkerState(marker),
            title = petShelter[0].name,
            snippet = "Iep que pasa contigo?",
            rotation = Float.MAX_VALUE

        )
        Marker(
            MarkerState(marker),
            title = "MARKER2",
            snippet = "Me lo estas preguntando a mi hermano?",
            rotation = Float.MIN_VALUE
        )
        Marker(
            MarkerState(marker),
            title = "MARKER3",
            snippet = "Que coño dices tu ahora?",
            flat = true
        )
        Marker(
            MarkerState(marker),
            title = "MARKER4",
            snippet = "No se, tu sabras",
        )
    }
}

