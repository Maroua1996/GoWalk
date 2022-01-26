package com.example.gowalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

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

import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

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
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // localisation
        val lat = 48.8534
        val long = 2.3488

        val currentCity = LatLng(lat,long)
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
}