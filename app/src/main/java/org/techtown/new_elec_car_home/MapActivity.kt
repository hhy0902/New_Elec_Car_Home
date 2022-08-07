package org.techtown.new_elec_car_home

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import org.techtown.new_elec_car_home.localModel.LoadMap
import org.techtown.new_elec_car_home.model.Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class MapActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener  {

    private var nowLat = 0.0
    private var nowLon = 0.0

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private var cancellationTokenSource : CancellationTokenSource? = null

    private val viewPager : ViewPager2 by lazy {
        findViewById(R.id.houseViewPager)
    }

    private val currentLocationButton : LocationButtonView by lazy {
        findViewById(R.id.currentLocationButton)
    }

    private lateinit var viewPagerAdapter : CarListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission()

        val tqee= "https://api.odcloud.kr/api/EvInfoServiceV2/v1/getEvSearchList?page=1&perPage=1000000&cond%5Baddr%3A%3ALIKE%5D=&serviceKey=JCrJa4%2F4eF07FKbnkSi7BDDUvnJXCE1CTiyt%2FfnxJ%2B7jewHaXTp5hrKQzOKdWYctQB%2B3a%2FHLuUHkTPq4hqrxvA%3D%3D"

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        val testList = mutableListOf<LatLng>()

        naverMap.mapType = NaverMap.MapType.Navi

        val uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = false
        uiSettings.logoGravity = Gravity.START
        uiSettings.logoGravity = Gravity.TOP
        uiSettings.setLogoMargin(20,20,0,0)

        currentLocationButton.map = naverMap

        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        val cameraUpdate = CameraUpdate.zoomTo(13.0)
        naverMap.moveCamera(cameraUpdate)

        val path = PathOverlay()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.odcloud.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        retrofitService.getElecCar().enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                if (response.isSuccessful) {
                    val item = response.body()
                    val itemList = item?.data

                    val id = "gxidgfi46e"
                    val secret = "bFEPj0IOi8yjUS88rNxTtT74ntkZZWjTzPxMX5PI"

                    viewPagerAdapter = CarListAdapter(buttonClicked = {
                        Toast.makeText(this@MapActivity, "${it.csNm}", Toast.LENGTH_SHORT).show()
                        Log.d("목적지 위치 : ", "${it.lat} / ${it.longi}")
                        Log.d("현재 위치 : ", "${nowLat} / ${nowLon}")

                        path.map = null
                        testList.clear()

                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://naveropenapi.apigw.ntruss.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                        val retrofitService = retrofit.create(RetrofitService::class.java)
                        retrofitService.getLoadMap(id, secret, "${nowLon},${nowLat}", "${it.longi},${it.lat}").enqueue(object :
                            Callback<LoadMap> {
                            override fun onResponse(call: Call<LoadMap>, response: Response<LoadMap>) {
                                if (response.isSuccessful) {
                                    val loadMap = response.body()
                                    val route = loadMap?.route?.traoptimal
                                    val navigation = loadMap?.route?.traoptimal?.firstOrNull()?.guide

//                                    val naviText = findViewById<TextView>(R.id.navigationTextView)

//                                    navigation?.forEach {
//                                        Log.d("navigation", "${it.instructions}")
//                                        naviText.append(it.instructions)
//                                        naviText.append("\n")
//                                        //naviText.text = ""+ "${it.instructions}"
//                                    }

                                    val pathLocation = route?.get(0)?.path
                                    pathLocation?.forEach {
                                        testList.add(LatLng(it.get(1), it.get(0)))
                                    }

                                    path.color = Color.BLUE
                                    path.coords = testList
                                    path.map = naverMap
                                }
                            }

                            override fun onFailure(call: Call<LoadMap>, t: Throwable) {

                            }

                        })
                    })

                    viewPager.adapter = viewPagerAdapter
                    viewPager.isUserInputEnabled = false

                    item?.data?.forEach {
                        val marker = Marker()
                        marker.position = LatLng(it.lat!!.toDouble(), it.longi!!.toDouble())
                        //marker.icon = Marker.DEFAULT_ICON
                        marker.icon = OverlayImage.fromResource(R.drawable.ic_baseline_ev_station_24)
                        marker.tag = it.cpId
                        marker.width = 90
                        marker.height = 90
                        marker.map = naverMap
                        //Log.d(" item lat / long", "${it.lat} ${it.longi}")
                        marker.onClickListener = this@MapActivity
                    }

                    response.body()?.let {
                        viewPagerAdapter.submitList(it.data)
                    }

                    findViewById<ProgressBar>(R.id.progressBar).visibility = INVISIBLE
                    findViewById<FrameLayout>(R.id.mapLayout).visibility = VISIBLE

                }
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {
                Log.d("testt onFailure ", "${t.message}")
                findViewById<TextView>(R.id.errorTextView).visibility = VISIBLE
                findViewById<ProgressBar>(R.id.progressBar).visibility = INVISIBLE
            }

        })
    }

    override fun onClick(overlay: Overlay): Boolean {
        overlay.tag

        val selectedModel = viewPagerAdapter.currentList.firstOrNull {
            it.cpId == overlay.tag
        }
        selectedModel?.let {
            val position = viewPagerAdapter.currentList.indexOf(it)
            viewPager.currentItem = position
            findViewById<ViewPager2>(R.id.houseViewPager).visibility = VISIBLE
            Toast.makeText(this, "${selectedModel?.addr}",Toast.LENGTH_SHORT).show()

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    val selectedModel = viewPagerAdapter.currentList.get(position)
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(selectedModel.lat!!.toDouble(), selectedModel.longi!!.toDouble()))
                        .animate(CameraAnimation.Easing)
                    naverMap.moveCamera(cameraUpdate)

                }
            })
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("testt", "승낙")

                fetchLocation()

            } else {
                Log.d("testt", "거부")
                //naverMap.locationTrackingMode = LocationTrackingMode.None
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        cancellationTokenSource = CancellationTokenSource()
        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource!!.token
        ).addOnSuccessListener { location ->
            try {
                nowLat = location.latitude
                nowLon = location.longitude
                Log.d("testt location ", "nowLat : ${nowLat}, nowLon : ${nowLon}")

//                naverMap.locationSource = locationSource
//                naverMap.locationTrackingMode = LocationTrackingMode.Follow

            } catch (e : Exception) {
                e.printStackTrace()
                Toast.makeText(this,"error 발생 다시 시도", Toast.LENGTH_SHORT).show()
            } finally {
                Log.d("testt finish","finish")

            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_ACCESS_LOCATION_PERMISSIONS
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource?.cancel()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 1000
    }
}


















































