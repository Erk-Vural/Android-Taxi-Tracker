package com.project.alpha.fragments

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.project.alpha.R
import com.project.alpha.dataClasses.Trip
import com.project.alpha.dataClasses.Zone
import com.project.alpha.directions.GoogleMapDTO
import okhttp3.OkHttpClient
import okhttp3.Request


class Type3 : Fragment() {
    var selectedDate = 0

    var longestTrip = Trip("", "", 0F, 0, 0)
    var currTrip = Trip("", "", 0F, 0, 0)

    var pickupZone = Zone(0, "", "", 0F, 0F)
    var dropZone = Zone(0, "", "", 0F, 0F)
    var currZone = Zone(0, "", "", 0F, 0F)

    val polylineOptions = PolylineOptions()

    fun findLongest(dayNo: Int, date: Int) {
        if (dayNo == date) {
            if (currTrip.tripDistance > longestTrip.tripDistance) {
                longestTrip.pickupDate = currTrip.pickupDate
                longestTrip.dropDate = currTrip.dropDate
                longestTrip.tripDistance = currTrip.tripDistance
                longestTrip.pickupLocId = currTrip.pickupLocId
                longestTrip.dropLocId = currTrip.dropLocId
            }
        }
    }

    fun findZone() {
        if (currZone.ID == longestTrip.pickupLocId) {
            pickupZone.ID = currZone.ID
            pickupZone.zone = currZone.zone
            pickupZone.borough = currZone.borough
            pickupZone.Lat = currZone.Lat
            pickupZone.Lot = currZone.Lot
        } else if (currZone.ID == longestTrip.dropLocId) {
            dropZone.ID = currZone.ID
            dropZone.zone = currZone.zone
            dropZone.borough = currZone.borough
            dropZone.Lat = currZone.Lat
            dropZone.Lot = currZone.Lot
        }
    }
    
    private fun getDirectionURL(start: LatLng, end: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${start.latitude},${start.longitude}&destination=${end.latitude},${end.longitude}&sensor=false&mode=driving&key=AIzaSyBFrHxNR62YfXcEaAJsf5N_oOsnsOPqvLE"
    }

    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()
            Log.d("GoogleMap", " data : $data")
            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path = ArrayList<LatLng>()

                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
//                    val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            for (i in result.indices) {
                polylineOptions.addAll(result[i])
                polylineOptions.width(8f)
                polylineOptions.color(Color.GREEN)
                polylineOptions.geodesic(true)
            }
        }
    }

    public fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }

    private val callback = OnMapReadyCallback { googleMap ->

        val pickupZ = LatLng(pickupZone.Lat.toDouble(), pickupZone.Lot.toDouble())
        val dropZ = LatLng(dropZone.Lat.toDouble(), dropZone.Lot.toDouble())

        val url = getDirectionURL(pickupZ, dropZ)
        GetDirection(url).execute()

        googleMap.addPolyline(polylineOptions)

        googleMap.addMarker(
            MarkerOptions().position(pickupZ).title(
                "Pick-up Location  Date: " +
                        longestTrip.pickupDate + " Distance: " + longestTrip.tripDistance
            )
        )
        googleMap.addMarker(
            MarkerOptions().position(dropZ).title(
                "Drop-off Location" +
                        longestTrip.dropDate + " Distance: " + longestTrip.tripDistance
            )
        )


        val zoomLevel = 10.0f //This goes up to 21
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupZ, zoomLevel))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tripDatabase = FirebaseDatabase.getInstance().getReference("tripData")

        val tripListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (i in dataSnapshot.children) {
                    var dayStr = i.child("tpep_pickup_datetime").value.toString()
                    var dayChar: String = dayStr.subSequence(0, 2) as String
                    var dayInt: Int = dayChar.toInt()

                    var dropDatestr = i.child("tpep_dropoff_datetime").value.toString()

                    val distanceStr = i.child("trip_distance").value.toString()
                    val distanceF = distanceStr.toFloat()

                    val pickupLocStr = i.child("PULocationID").value.toString()
                    val pickupLocInt = pickupLocStr.toInt()

                    val dropLocStr = i.child("DOLocationID").value.toString()
                    val dropLocInt = dropLocStr.toInt()

                    currTrip.pickupDate = dayStr
                    currTrip.dropDate = dropDatestr
                    currTrip.tripDistance = distanceF
                    currTrip.pickupLocId = pickupLocInt
                    currTrip.dropLocId = dropLocInt

                    findLongest(dayInt, selectedDate)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        val zoneDatabase = FirebaseDatabase.getInstance().getReference("zone")

        val zoneListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (i in dataSnapshot.children) {
                    val locIdStr = i.child("LocationID").value.toString()
                    val locIdInt = locIdStr.toInt()

                    val zoneStr = i.child("Zone").value.toString()

                    val borough = i.child("borough").value.toString()

                    val latStr = i.child("Lat").value.toString()
                    val latF = latStr.toFloat()

                    val lotStr = i.child("Lot").value.toString()
                    val lotF = lotStr.toFloat()

                    currZone.ID = locIdInt
                    currZone.zone = zoneStr
                    currZone.borough = borough
                    currZone.Lat = latF
                    currZone.Lot = lotF

                    findZone()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        view.findViewById<Button>(R.id.type_3_button).setOnClickListener {
            val tripSpinner: Spinner = view.findViewById(R.id.longest_trip) as Spinner
            val selectedDateStr: String = tripSpinner.selectedItem.toString()
            selectedDate = selectedDateStr.toInt()


            tripDatabase.addValueEventListener(tripListener)
            zoneDatabase.addValueEventListener(zoneListener)

            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_type_3, container, false)
    }
}