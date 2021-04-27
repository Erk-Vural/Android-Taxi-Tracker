package com.project.alpha.dataClasses

data class Trip(
    var pickupDate: String,
    var dropDate: String,
    var tripDistance: Float,
    var pickupLocId: Int,
    var dropLocId: Int
)
