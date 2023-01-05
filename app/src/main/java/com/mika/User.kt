package com.mika


data class User(
    val uuID: String? = null,
    var userName: String? = null,
    val userType: String? = null,
    val email: String? = null,
    var plateNumber: String? = null,
    var phoneNumber: String? = null,
    var vehicleType: String? = null,
    var vehicleColor: String? = null
)