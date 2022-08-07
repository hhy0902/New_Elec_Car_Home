package org.techtown.new_elec_car_home.localModel

import com.google.gson.annotations.SerializedName

data class Route(
    @SerializedName("traoptimal")
    val traoptimal: List<Traoptimal>?
)