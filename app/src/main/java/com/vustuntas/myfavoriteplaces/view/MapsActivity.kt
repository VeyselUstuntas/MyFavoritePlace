package com.vustuntas.myfavoriteplaces.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.vustuntas.myfavoriteplaces.R
import com.vustuntas.myfavoriteplaces.adapter.SingletonClass
import com.vustuntas.myfavoriteplaces.databinding.ActivityMapsBinding
import com.vustuntas.myfavoriteplaces.model.Places
import com.vustuntas.myfavoriteplaces.roomDb.PlaceDao
import com.vustuntas.myfavoriteplaces.roomDb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener

    private lateinit var permissionLauncher : ActivityResultLauncher<String>

    private var clickedLongCordinatesLati : Double? = null
    private var clickedLongCordinatesLong : Double? = null

    private lateinit var sharedPreferences : SharedPreferences
    private var trackLocation : Boolean? = null

    private lateinit var db : PlaceDatabase
    private lateinit var placeDao : PlaceDao
    private var compositeDisposable = CompositeDisposable()

    private var singletonClass = SingletonClass.place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = this.getSharedPreferences("com.vustuntas.myfavoriteplaces", MODE_PRIVATE)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerPermission()
        trackLocation = sharedPreferences.getBoolean("trackLoc",false)

        db = Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places").build()
        placeDao = db.placeDao()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val destinationOptionsMenu = intent.getStringExtra("optionsMenu")
        val destinationRecycler = intent.getStringExtra("recycler")
        if(destinationOptionsMenu != null && destinationOptionsMenu.equals("optionsMenu")){
            mMap.setOnMapLongClickListener(this)
            binding.delete.visibility = View.GONE
            binding.save.isEnabled = false
            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
            locationListener = object : LocationListener{
                override fun onLocationChanged(p0: Location) {
                    if(!trackLocation!!){
                        if(ContextCompat.checkSelfPermission(this@MapsActivity,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                            mMap.isMyLocationEnabled = true
                        val userLocation = LatLng(p0.latitude,p0.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                        sharedPreferences.edit().putBoolean("trackLoc",true).apply()
                    }
                }
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                getPermission()
            }
            else{
                getPermission()
            }
        }
        else if(destinationRecycler!= null && destinationRecycler.equals("recycler")) {
            binding.save.visibility = View.GONE
            binding.placeNameTextView.isEnabled = false
            val placeObject = singletonClass.obje
            if(placeObject != null){
                binding.placeNameTextView.setText(placeObject.name)
                val latLoc = LatLng(placeObject.latitude,placeObject.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLoc,16f))
                mMap.addMarker(MarkerOptions().position(latLoc).title("Clicked Location '${placeObject.name}'"))
            }
        }

    }

    fun save(view:View){
        if(clickedLongCordinatesLati != null && clickedLongCordinatesLong != null){
            val placeObject = Places(binding.placeNameTextView.text.toString(),clickedLongCordinatesLati!!,clickedLongCordinatesLong!!)
            compositeDisposable.add(
                placeDao.insert(placeObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }

    }
    private fun handleResponse(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
    fun delete(view:View){
        val deletePlaceObject = singletonClass.obje
        if(deletePlaceObject != null){
            compositeDisposable.add(
                placeDao.delete(deletePlaceObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }

    }



    private fun registerPermission(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                if(ContextCompat.checkSelfPermission(this@MapsActivity,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    val userLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(userLastLocation != null){
                        val lastLoc = LatLng(userLastLocation.latitude,userLastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc,16f))
                        mMap.isMyLocationEnabled = true
                    }
                }
            }
            else{
                Toast.makeText(this@MapsActivity,"İzin verilmedi....",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun getPermission(){
        if(ContextCompat.checkSelfPermission(this@MapsActivity,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                //izin verilmemiş izin iste
                Snackbar.make(binding.root,"İzin Verilmemiş İzin Veriyor musun?",Snackbar.LENGTH_INDEFINITE).setAction("Evet"){
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }
            else{
                //izin verilmemiş izin iste
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        else{
            //izin verilmişse
            if(ContextCompat.checkSelfPermission(this@MapsActivity,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                val userLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(userLastLocation != null){
                    val lastLoc = LatLng(userLastLocation.latitude,userLastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc,16f))
                    mMap.isMyLocationEnabled = true
                }
            }
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        binding.save.isEnabled = true
        mMap.addMarker(MarkerOptions().position(p0).title("Clicked Cordinates"))
        clickedLongCordinatesLati = p0.latitude
        clickedLongCordinatesLong = p0.longitude

    }

    override fun onDestroy() {

        compositeDisposable.clear()
        super.onDestroy()
    }
}