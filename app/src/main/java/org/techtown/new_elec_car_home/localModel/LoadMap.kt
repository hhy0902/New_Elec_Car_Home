package org.techtown.new_elec_car_home.localModel

import com.google.gson.annotations.SerializedName

data class LoadMap(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("currentDateTime")
    val currentDateTime: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("route")
    val route: Route?
)