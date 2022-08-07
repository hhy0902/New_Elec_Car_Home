package org.techtown.new_elec_car_home.model

import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("addr")
    val addr: String?,
    @SerializedName("chargeTp")
    val chargeTp: String?,
    @SerializedName("cpId")
    val cpId: Int?,
    @SerializedName("cpNm")
    val cpNm: String?,
    @SerializedName("cpStat")
    val cpStat: String?,
    @SerializedName("cpTp")
    val cpTp: String?,
    @SerializedName("csId")
    val csId: Int?,
    @SerializedName("csNm")
    val csNm: String?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("longi")
    val longi: String?,
    @SerializedName("statUpdatetime")
    val statUpdatetime: String?
)