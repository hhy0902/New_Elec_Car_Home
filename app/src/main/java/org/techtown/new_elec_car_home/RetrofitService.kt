package org.techtown.new_elec_car_home

import org.techtown.new_elec_car_home.localModel.LoadMap
import org.techtown.new_elec_car_home.model.Item
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitService {
    @GET("api/EvInfoServiceV2/v1/getEvSearchList?page=1&perPage=1000000&cond%5Baddr%3A%3ALIKE%5D=&serviceKey=JCrJa4%2F4eF07FKbnkSi7BDDUvnJXCE1CTiyt%2FfnxJ%2B7jewHaXTp5hrKQzOKdWYctQB%2B3a%2FHLuUHkTPq4hqrxvA%3D%3D")
    fun getElecCar(
    ) : Call<Item>

    @GET("map-direction/v1/driving?")
    fun getLoadMap(
        @Header("X-NCP-APIGW-API-KEY-ID") id : String,
        @Header("X-NCP-APIGW-API-KEY") secret : String,
        @Query("start") start : String,
        @Query("goal") goal : String
    ) : Call<LoadMap>
}











































