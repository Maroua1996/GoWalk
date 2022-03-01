package com.example.gowalk

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.gowalk.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    // location current I
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

      // current localisation II
      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
      // current localisation III
        fetchLocation()

      // current localisation V
      findViewById<Button>(R.id.btn_get_location).setOnClickListener{
          fetchLocation()
      }
    }




    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // localisation
        val long =
        val latitude =


        val currentCity = LatLng(long,latitude)
        /* zoom de la caméra */
        val zoomLevel = 15f

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentCity,zoomLevel))
       // icon de position
        mMap.addMarker(MarkerOptions().position(currentCity))

        // Une fois que la map est affichée appel des functions
        setMapLongClick(mMap)
        setPoiClick(mMap)

    }

    //menu pour le type de map

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options,menu)
        return true
    }

    //swich case
    override fun onOptionsItemSelected(item: MenuItem)= when(item.itemId){
        R.id.normal_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)

    }

    // event lorsque l'on clique  longtemps sur un endroit (creation de marqueur et message) (maybe même principe pour le bouton localisation)
    private fun setMapLongClick(map:GoogleMap){
        map.setOnMapLongClickListener {
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Lng: %2$.5f",
                it.latitude,
                it.longitude
            )

        map.addMarker(MarkerOptions().position(it).title("Votre position" ).snippet(snippet))
            // connecter à firebase et recupérer long et lat pour la realtime database
            val database = Firebase.database("https://android-gowalk-54707-default-rtdb.europe-west1.firebasedatabase.app/")
            val reference = database.reference
            val  data = reference.push().child("location").setValue(it)
    }
    }
    //Connaitre les infos sur le lieu
    private fun setPoiClick(map: GoogleMap){
        map.setOnPoiClickListener { point ->
            val poiMarker = map.addMarker(MarkerOptions().position(point.latLng).title(point.name))

            if (poiMarker != null) {
                poiMarker.showInfoWindow()
            }


        }
    }

   // current localisation IV
    private fun fetchLocation(){
        val task = fusedLocationProviderClient.lastLocation

       if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
          != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        }

       /*task.addOnSuccessListener {
           if(it != null){
               Toast.makeText(applicationContext,"${it.latitude} - ${it.longitude}", Toast.LENGTH_SHORT).show()
               return it
           }
       }*/
        return it
    }

}