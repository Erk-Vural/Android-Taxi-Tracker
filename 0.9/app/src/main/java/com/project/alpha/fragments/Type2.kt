package com.project.alpha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.project.alpha.dataClasses.Day
import com.project.alpha.R
import java.lang.StringBuilder


class Type2 : Fragment() {
    var startDateInt = 0
    var endDateInt = 0

    var day1 = Day("", 100F)
    var day2 = Day("", 100F)
    var day3 = Day("", 100F)
    var day4 = Day("", 100F)
    var day5 = Day("", 100F)

    var currDay = Day("", 0F)

    fun isBetweenDates(dayNo: Int, sDay: Int, eDay: Int) {
        if (dayNo in sDay..eDay && currDay.tripDistance > 0F) {
            if (currDay.tripDistance < day1.tripDistance) {
                day1.date = currDay.date
                day1.tripDistance = currDay.tripDistance
            } else if (currDay.tripDistance < day2.tripDistance) {
                day2.date = currDay.date
                day2.tripDistance = currDay.tripDistance
            } else if (currDay.tripDistance < day3.tripDistance) {
                day3.date = currDay.date
                day3.tripDistance = currDay.tripDistance
            } else if (currDay.tripDistance < day4.tripDistance) {
                day4.date = currDay.date
                day4.tripDistance = currDay.tripDistance
            } else if (currDay.tripDistance < day5.tripDistance) {
                day5.date = currDay.date
                day5.tripDistance = currDay.tripDistance
            }
        }
    }

    var sb = StringBuilder()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = FirebaseDatabase.getInstance().getReference("tripData")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (i in dataSnapshot.children) {
                    var dayStr = i.child("tpep_dropoff_datetime").value.toString()
                    var dayChar: String = dayStr.subSequence(0,2) as String
                    var dayInt:Int = dayChar.toInt()

                    val distanceStr = i.child("trip_distance").value.toString()
                    val distanceF = distanceStr.toFloat()

                    currDay.date = dayStr
                    currDay.tripDistance = distanceF

                    isBetweenDates(dayInt, startDateInt, endDateInt)
                }
                sb.append("Date: ${day1.date} Trip Distance: ${day1.tripDistance}\n")
                sb.append("Date: ${day2.date} Trip Distance: ${day2.tripDistance}\n")
                sb.append("Date: ${day3.date} Trip Distance: ${day3.tripDistance}\n")
                sb.append("Date: ${day4.date} Trip Distance: ${day4.tripDistance}\n")
                sb.append("Date: ${day5.date} Trip Distance: ${day5.tripDistance}\n")

                val textView: TextView = view.findViewById(R.id.fiveTrips) as TextView
                textView.text = sb
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        view.findViewById<Button>(R.id.type_2_button).setOnClickListener {
            val startSpinner : Spinner = view.findViewById(R.id.startDate) as Spinner
            val startDateStr: String = startSpinner.selectedItem.toString()
            this.startDateInt = startDateStr.toInt()

            val endSpinner : Spinner = view.findViewById(R.id.endDate) as Spinner
            val endDateStr: String = endSpinner.selectedItem.toString()
            this.endDateInt = endDateStr.toInt()

            database.addValueEventListener(postListener)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_type_2, container, false)
    }
}


