package com.mid.mad_final

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Track_location : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var mapFragment: SupportMapFragment? = null
    private var latLng: LatLng? = null
    private var long = ArrayList<Double>()
    private var lat = ArrayList<Double>()
    var a: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_location)
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)

        val fb = FirebaseDatabase.getInstance().reference
        val fm = fb.child("Guardian/Talha/Family_Talha")

        System.out.println("HHHHHHHHheeeeeeeeeeeeeeeellllllllllllllloooooooooo ================ "+fm)
        fm.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                upload(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun upload(snapshot: DataSnapshot) {

        for (child in snapshot.children) {
            for (child2 in child.children) {
                if (child2.hasChild("LatLng")) {
                    for(child3 in child2.children){
                        System.out.println("HHHHHHHHheeeeeeeeeeeeeeeellllllllllllllloooooooooo ================ "+child3)
                        val ltlng = child2.getValue(LatLng::class.java)
                        long[a] = ltlng!!.longitude
                        lat[a] = ltlng!!.latitude
                        a++
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocations()
    }

    private fun getLocations() {
//        for (item in long.indices) {
            val cameraPosition =
                CameraPosition.Builder().target(LatLng(33.2298558, 73.1461473)).zoom(8.0f)
                    .build()
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(33.2298558, 73.1461473))
                    .title("Ali")
            )
//        }
    }
}