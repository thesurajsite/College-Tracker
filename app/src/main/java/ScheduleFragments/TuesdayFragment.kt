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

class TuesdayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_tuesday, container, false)

        val arrScheduleTuesday=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        arrScheduleTuesday.add(ScheduleModel(0,"tuesday","Mathematics","09:00"))
        arrScheduleTuesday.add(ScheduleModel(0,"tuesday","English","10:00"))
        arrScheduleTuesday.add(ScheduleModel(0,"tuesday","VEEES","11:00"))
        arrScheduleTuesday.add(ScheduleModel(0,"tuesday","Thermodynamics","12:00"))
        arrScheduleTuesday.add(ScheduleModel(0,"tuesday","---Tuesday---","01:00"))
        arrScheduleTuesday.add(ScheduleModel(0,"tuesday","Geology","02:00"))


        val tuesdayRecyclerView = view.findViewById<RecyclerView>(R.id.TuesdayRecyclerView)
        val scheduleAdapter= RecyclerScheduleAdapter(requireContext(), arrScheduleTuesday)
        tuesdayRecyclerView.adapter=scheduleAdapter
        tuesdayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

        floatingActionButton.setOnClickListener {
            Toast.makeText(context, "tuesday", Toast.LENGTH_SHORT).show()
        }



        return view
    }


}