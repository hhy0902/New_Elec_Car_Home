package org.techtown.new_elec_car_home.localModel

import com.google.gson.annotations.SerializedName

data class Goal(
    @SerializedName("dir")
    val dir: Int?,
    @SerializedName("location")
    val location: List<Double>?
)