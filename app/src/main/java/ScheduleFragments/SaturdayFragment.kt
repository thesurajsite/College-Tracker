package ScheduleFragments

import ScheduleRecyclerView.RecyclerScheduleAdapter
import ScheduleRecyclerView.ScheduleItemClickListener
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


class SaturdayFragment : Fragment() , ScheduleItemClickListener {
    private val arrScheduleSaturday = ArrayList<ScheduleModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_saturday, container, false)

        //val arrScheduleSaturday=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        arrScheduleSaturday.add(ScheduleModel(0,"saturday","Mathematics","09:00"))
        arrScheduleSaturday.add(ScheduleModel(0,"saturday","English","10:00"))
        arrScheduleSaturday.add(ScheduleModel(0,"saturday","VEEES","11:00"))
        arrScheduleSaturday.add(ScheduleModel(0,"saturday","Thermodynamics","12:00"))
        arrScheduleSaturday.add(ScheduleModel(0,"saturday","---Saturday---","01:00"))
        arrScheduleSaturday.add(ScheduleModel(0,"saturday","Geology","02:00"))



        val saturdayRecyclerView = view.findViewById<RecyclerView>(R.id.SaturdayRecyclerView)
        val scheduleAdapter= RecyclerScheduleAdapter(requireContext(), this, arrScheduleSaturday)
        saturdayRecyclerView.adapter=scheduleAdapter
        saturdayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

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
                    arrScheduleSaturday.add(ScheduleModel(0,"saturday",lectureName,timeName))

                    scheduleAdapter.notifyItemChanged(arrScheduleSaturday.size-1)
                    saturdayRecyclerView.scrollToPosition(arrScheduleSaturday.size-1)
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

    override fun onEditScheduleClicked(position: Int) {
        // Handle edit schedule click in Saturday fragment
        // You can open a dialog or perform any specific action here
        val editedSchedule = arrScheduleSaturday[position]
        val xyz: String = arrScheduleSaturday[position].day
        val abc: String = arrScheduleSaturday[position].subject
        // Example: Open a dialog with the schedule details for editing
        Toast.makeText(context, xyz + abc, Toast.LENGTH_SHORT).show()
//        openEditScheduleDialog(editedSchedule)
    }

//    override fun onDeleteScheduleClicked(position: Int) {
//        // Handle delete schedule click in Saturday fragment
//        // You can open a dialog or perform any specific action here
//        val deletedSchedule = arrScheduleSaturday[position]
//        // Example: Open a dialog for confirmation before deleting
//        openDeleteScheduleDialog(deletedSchedule)
//    }
//
//    private fun openEditScheduleDialog(scheduleModel: ScheduleModel) {
//        // Implement the dialog for editing the schedule here
//    }
//
//    private fun openDeleteScheduleDialog(scheduleModel: ScheduleModel) {
//        // Implement the dialog for confirming deletion here
//    }



}