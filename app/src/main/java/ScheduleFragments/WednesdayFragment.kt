package ScheduleFragments

import ScheduleRecyclerView.RecyclerScheduleAdapter
import ScheduleRecyclerView.ScheduleModel
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
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
            vibrator.vibrate(50)
            val dialog= Dialog(requireContext())
            dialog.setContentView(R.layout.add_update_schedule)

            val addLecture=dialog.findViewById<EditText>(R.id.addLecture)
            val addTime=dialog.findViewById<EditText>(R.id.addTime)
            val addSchedule=dialog.findViewById<ImageView>(R.id.addSchedule)
            val deleteSchedule=dialog.findViewById<ImageView>(R.id.deleteSchedule)

            addSchedule.setOnClickListener {
                vibrator.vibrate(50)
                var lectureName=""
                var timeName=""

                lectureName=addLecture.text.toString()
                timeName=addTime.text.toString()

                if(lectureName!="")
                {
                    arrScheduleWednesday.add(ScheduleModel(0,"wednesday",lectureName,timeName))

                    scheduleAdapter.notifyItemChanged(arrScheduleWednesday.size-1)
                    wednesdayRecyclerView.scrollToPosition(arrScheduleWednesday.size-1)
                    dialog.dismiss()

                }
                else{
                    Toast.makeText(context, "Lecture Can't be Empty", Toast.LENGTH_SHORT).show()
                }

            }

            deleteSchedule.setOnClickListener {
                vibrator.vibrate(50)
                Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

            }


            dialog.show()

        }


        return view
    }


}