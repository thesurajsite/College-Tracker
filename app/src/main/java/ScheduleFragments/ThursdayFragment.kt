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

class ThursdayFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_thursday, container, false)

        val arrScheduleThursday=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        arrScheduleThursday.add(ScheduleModel(0,"thursday","Mathematics","09:00"))
        arrScheduleThursday.add(ScheduleModel(0,"thursday","English","10:00"))
        arrScheduleThursday.add(ScheduleModel(0,"thursday","VEEES","11:00"))
        arrScheduleThursday.add(ScheduleModel(0,"thursday","Thermodynamics","12:00"))
        arrScheduleThursday.add(ScheduleModel(0,"thursday","---thursday---","01:00"))
        arrScheduleThursday.add(ScheduleModel(0,"thursday","Geology","02:00"))



        val thursdayRecyclerView = view.findViewById<RecyclerView>(R.id.ThursdayRecyclerView)
        val scheduleAdapter= RecyclerScheduleAdapter(requireContext(), arrScheduleThursday)
        thursdayRecyclerView.adapter=scheduleAdapter
        thursdayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

        floatingActionButton.setOnClickListener {
            Toast.makeText(context, "thursday", Toast.LENGTH_SHORT).show()
        }











        return view
    }


}