package com.sotrh.geocodeapp.model

/**
 * Created by benjamin on 10/11/17
 */
data class AddressResponse(
        val results: List<AddressResult>,
        val error_message: String?,
        val status: String
)

data class AddressResult(
        val address_components: List<AddressComponent>,
        val formatted_address: String,
        val geometry: Geometry
)

data class AddressComponent(
        val long_name: String,
        val short_name: String,
        val types: List<String>
)

data class Geometry(
        val bounds: Map<String, LatLng>,
        val location: LatLng,
        val location_type: String,
        val viewport: Map<String, LatLng>,
        val place_id: String,
        val types: List<String>
)

data class LatLng(
        val lat: Double,
        val lng: Double
)