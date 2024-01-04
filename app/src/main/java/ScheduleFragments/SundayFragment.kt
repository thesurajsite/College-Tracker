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
import com.google.android.material.tabs.TabItem


class SundayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_sunday, container, false)

        val arrScheduleSunday=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        arrScheduleSunday.add(ScheduleModel(0,"sunday","Mathematics","09:00"))
        arrScheduleSunday.add(ScheduleModel(0,"sunday","English","10:00"))
        arrScheduleSunday.add(ScheduleModel(0,"sunday","VEEES","11:00"))
        arrScheduleSunday.add(ScheduleModel(0,"sunday","Thermodynamics","12:00"))
        arrScheduleSunday.add(ScheduleModel(0,"sunday","---Sunday---","01:00"))
        arrScheduleSunday.add(ScheduleModel(0,"sunday","Geology","02:00"))



        val sundayRecyclerView = view.findViewById<RecyclerView>(R.id.SundayRecyclerView)
        val scheduleAdapter= RecyclerScheduleAdapter(requireContext(), arrScheduleSunday)
        sundayRecyclerView.adapter=scheduleAdapter
        sundayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

        floatingActionButton.setOnClickListener {
            Toast.makeText(context, "sunday", Toast.LENGTH_SHORT).show()
        }












        return view

    }


}


