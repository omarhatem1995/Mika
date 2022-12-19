package com.mika


data class User(
    val uuID: String? = null,
    var userName: String? = null,
    val userType: String? = null,
    val email: String? = null,
    val plateNumber: String? = null,
    var phoneNumber: String? = null,
    val vehicleType: String? = null,
    val vehicleColor: String? = null
)