package com.project.alpha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.alpha.dataClasses.Day
import com.project.alpha.R


class Type1 : Fragment() {
    var day1 = Day("", 0F)
    var day2 = Day("", 0F)
    var day3 = Day("", 0F)
    var day4 = Day("", 0F)
    var day5 = Day("", 0F)

    var currDay = Day("", 0F)

    var sb = StringBuilder()

    fun cmpDistance() {
        if (currDay.tripDistance > day1.tripDistance) {
            day1.date = currDay.date
            day1.tripDistance = currDay.tripDistance
        } else if (currDay.tripDistance > day2.tripDistance) {
            day2.date = currDay.date
            day2.tripDistance = currDay.tripDistance
        } else if (currDay.tripDistance > day3.tripDistance) {
            day3.date = currDay.date
            day3.tripDistance = currDay.tripDistance
        } else if (currDay.tripDistance > day4.tripDistance) {
            day4.date = currDay.date
            day4.tripDistance = currDay.tripDistance
        } else if (currDay.tripDistance > day5.tripDistance) {
            day5.date = currDay.date
            day5.tripDistance = currDay.tripDistance
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = FirebaseDatabase.getInstance().getReference("tripData")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (i in dataSnapshot.children) {
                    var dayStr = i.child("tpep_dropoff_datetime").value.toString()

                    val distanceStr = i.child("trip_distance").value.toString()
                    val distanceF = distanceStr.toFloat()

                    currDay.date = dayStr
                    currDay.tripDistance = distanceF

                    cmpDistance()
                }
                sb.append("Date: ${day1.date} Trip Distance: ${day1.tripDistance}\n")
                sb.append("Date: ${day2.date} Trip Distance: ${day2.tripDistance}\n")
                sb.append("Date: ${day3.date} Trip Distance: ${day3.tripDistance}\n")
                sb.append("Date: ${day4.date} Trip Distance: ${day4.tripDistance}\n")
                sb.append("Date: ${day5.date} Trip Distance: ${day5.tripDistance}\n")

                val textView: TextView = view.findViewById(R.id.fiveDays) as TextView
                textView.text = sb
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        view.findViewById<Button>(R.id.type_1_button).setOnClickListener {
            database.addValueEventListener(postListener)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_type_1, container, false)
    }

}