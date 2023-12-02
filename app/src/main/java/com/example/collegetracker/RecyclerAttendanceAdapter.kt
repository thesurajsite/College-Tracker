package com.example.collegetracker


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class RecyclerAttendanceAdapter(val context: Context,val arrAttendance: ArrayList<AttendenceModel>) : RecyclerView.Adapter<RecyclerAttendanceAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        val subject = itemView.findViewById<TextView>(R.id.subject)
        val conductNumber=itemView.findViewById<TextView>(R.id.conductNumber)
        val attendNumber=itemView.findViewById<TextView>(R.id.attendNumber)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.attendance_row, parent, false))

    }

    override fun getItemCount(): Int {
        return arrAttendance.size


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.subject.text=arrAttendance[position].subject
        holder.attendNumber.text=arrAttendance[position].attended
        holder.conductNumber.text=arrAttendance[position].conducted



    }




}
