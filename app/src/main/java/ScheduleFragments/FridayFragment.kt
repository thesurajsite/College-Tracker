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


class FridayFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_friday, container, false)

        val arrScheduleFriday=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        arrScheduleFriday.add(ScheduleModel(0,"friday","Mathematics","09:00"))
        arrScheduleFriday.add(ScheduleModel(0,"friday","English","10:00"))
        arrScheduleFriday.add(ScheduleModel(0,"friday","VEEES","11:00"))
        arrScheduleFriday.add(ScheduleModel(0,"friday","Thermodynamics","12:00"))
        arrScheduleFriday.add(ScheduleModel(0,"friday","---friday---","01:00"))
        arrScheduleFriday.add(ScheduleModel(0,"friday","Geology","02:00"))



        val fridayRecyclerView = view.findViewById<RecyclerView>(R.id.FridayRecyclerView)
        val scheduleAdapter= RecyclerScheduleAdapter(requireContext(), arrScheduleFriday)
        fridayRecyclerView.adapter=scheduleAdapter
        fridayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

        floatingActionButton.setOnClickListener {
            Toast.makeText(context, "friday", Toast.LENGTH_SHORT).show()
        }





        return view
    }


}