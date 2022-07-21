package com.mid.mad_final

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my__location.*
import java.io.IOException
import java.util.*

@Suppress("DEPRECATION")
class My_Location : AppCompatActivity() , OnMapReadyCallback {
    val location_image: Int = 1234
    private lateinit var uri: Uri
    var Fname: String = ""
    var pass: String = ""
    var imgid : String = ""
    private var filePath: Uri? = null
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mapFragment: SupportMapFragment? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback : LocationCallback? = null
    private var latLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my__location)
        pick_image.setOnClickListener { open() }
        submit.setOnClickListener{ verify()}

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocationAccess()
    }
    fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            getLocationUpdates()
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_FINE_LOCATION)
        }
    }

    private fun getLocationUpdates() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.setInterval(5000) //5 seconds
        mLocationRequest!!.setFastestInterval(3000) //3 seconds
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        mLocationRequest!!.setSmallestDisplacement(0.1F); //1/10 meter

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        latLng = LatLng(location.latitude, location.longitude)
                    }
                }
            }
        }
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_FINE_LOCATION
            )
            return
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest!!, mLocationCallback!!, null)
    }
    companion object {
        private val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111
    }
    private fun verify() {
        Fname = f_name.text.toString()
        pass = f_password.text.toString()
        if (TextUtils.isEmpty(Fname) && TextUtils.isEmpty(pass) && uri != null) {
            f_name.error = "Required"
            f_password.error = "Required"
        }
        val fb = FirebaseDatabase.getInstance().reference
        val fm = fb.child("Guardian/$Fname")
        val members = fm.orderByChild("Type")
        members.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { upload(snapshot) }
            override fun onCancelled(error: DatabaseError) { TODO("Not yet implemented") }
        })
    }
    private fun upload(snapshot: DataSnapshot) {
        val fb1 = FirebaseDatabase.getInstance().reference
        for (child in snapshot.children) {
            val member = child.getValue(Family_Member::class.java)
            if (member!!.Type == "Family Member" && member.FM_Password == pass && member.Name == "$Fname") {
                Toast.makeText(this, "Entered", Toast.LENGTH_LONG).show()
                val table = fb1.child("Guardian/$Fname")
                val bart = table.child("Family_$Fname")
                bart.child("Image").setValue(imgid)
                bart.child("LatLng").setValue(latLng)
            }
            else {
                Toast.makeText(this, "Not Entered", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun open() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), location_image) }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == location_image && resultCode == Activity.RESULT_OK) {
            try {
                filePath = data!!.data
                val bitmap = MediaStore.Images.Media
                    .getBitmap(
                        contentResolver,
                        filePath
                    )
                location.setImageBitmap(bitmap)
                storage = FirebaseStorage.getInstance();
                storageReference = storage!!.getReference();
                imgid = UUID.randomUUID().toString()
                val ref: StorageReference = storageReference!!.child("images/$imgid")
                ref.putFile(filePath!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}

