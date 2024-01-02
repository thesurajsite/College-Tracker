package ScheduleFragments

import RecyclerView.AttendenceModel
import ScheduleRecyclerView.RecyclerScheduleAdapter
import ScheduleRecyclerView.ScheduleModel
import android.app.Dialog
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MondayFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_monday, container, false)

        val arrSchedule=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator


        arrSchedule.add(ScheduleModel(1,"Mathematics","09:00"))
        arrSchedule.add(ScheduleModel(1,"English","10:00"))
        arrSchedule.add(ScheduleModel(1,"VEEES","11:00"))
        arrSchedule.add(ScheduleModel(1,"Thermodynamics","12:00"))
        arrSchedule.add(ScheduleModel(1,"---Break---","01:00"))
        arrSchedule.add(ScheduleModel(1,"Geology","02:00"))


        val mondayRecyclerView = view.findViewById<RecyclerView>(R.id.MondayRecyclerView)
        val scheduleAdapter=RecyclerScheduleAdapter(requireContext(), arrSchedule)
        mondayRecyclerView.adapter=scheduleAdapter
        mondayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

        floatingActionButton.setOnClickListener {
            vibrator.vibrate(50)
            val dialog= Dialog(requireContext())
            dialog.setContentView(R.layout.add_update_schedule)





            dialog.show()

        }








        return view


    }


}