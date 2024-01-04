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


class SaturdayFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_saturday, container, false)

        val arrScheduleSaturday=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        arrScheduleSaturday.add(ScheduleModel(0,"saturday","Mathematics","09:00"))
        arrScheduleSaturday.add(ScheduleModel(0,"saturday","English","10:00"))
        arrScheduleSaturday.add(ScheduleModel(0,"saturday","VEEES","11:00"))
        arrScheduleSaturday.add(ScheduleModel(0,"saturday","Thermodynamics","12:00"))
        arrScheduleSaturday.add(ScheduleModel(0,"saturday","---Saturday---","01:00"))
        arrScheduleSaturday.add(ScheduleModel(0,"saturday","Geology","02:00"))



        val saturdayRecyclerView = view.findViewById<RecyclerView>(R.id.SaturdayRecyclerView)
        val scheduleAdapter= RecyclerScheduleAdapter(requireContext(), arrScheduleSaturday)
        saturdayRecyclerView.adapter=scheduleAdapter
        saturdayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

        floatingActionButton.setOnClickListener {
            Toast.makeText(context, "Saturday", Toast.LENGTH_SHORT).show()
        }


        return view
    }


}