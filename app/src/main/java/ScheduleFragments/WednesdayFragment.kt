package ScheduleFragments

import ScheduleRecyclerView.RecyclerScheduleAdapter
import ScheduleRecyclerView.ScheduleModel
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class WednesdayFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_wednesday, container, false)

        val arrScheduleWednesday=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        arrScheduleWednesday.add(ScheduleModel(0,"wednesday","Mathematics","09:00"))
        arrScheduleWednesday.add(ScheduleModel(0,"wednesday","English","10:00"))
        arrScheduleWednesday.add(ScheduleModel(0,"wednesday","VEEES","11:00"))
        arrScheduleWednesday.add(ScheduleModel(0,"wednesday","Thermodynamics","12:00"))
        arrScheduleWednesday.add(ScheduleModel(0,"wednesday","---wednesday---","01:00"))
        arrScheduleWednesday.add(ScheduleModel(0,"wednesday","Geology","02:00"))


        val wednesdayRecyclerView = view.findViewById<RecyclerView>(R.id.WednesdayRecyclerView)
        val scheduleAdapter= RecyclerScheduleAdapter(requireContext(), arrScheduleWednesday)
        wednesdayRecyclerView.adapter=scheduleAdapter
        wednesdayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

        floatingActionButton.setOnClickListener {
            Toast.makeText(context, "Wednesday", Toast.LENGTH_SHORT).show()
        }


        return view
    }


}