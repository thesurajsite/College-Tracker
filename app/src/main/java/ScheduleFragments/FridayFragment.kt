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


class FridayFragment : Fragment(), ScheduleItemClickListener {
    private val arrScheduleFriday = ArrayList<ScheduleModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_friday, container, false)

     //   val arrScheduleFriday=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        arrScheduleFriday.add(ScheduleModel(0,"friday","Mathematics","09:00"))
        arrScheduleFriday.add(ScheduleModel(0,"friday","English","10:00"))
        arrScheduleFriday.add(ScheduleModel(0,"friday","VEEES","11:00"))
        arrScheduleFriday.add(ScheduleModel(0,"friday","Thermodynamics","12:00"))
        arrScheduleFriday.add(ScheduleModel(0,"friday","---friday---","01:00"))
        arrScheduleFriday.add(ScheduleModel(0,"friday","Geology","02:00"))



        val fridayRecyclerView = view.findViewById<RecyclerView>(R.id.FridayRecyclerView)
        val scheduleAdapter= RecyclerScheduleAdapter(requireContext(),  this, arrScheduleFriday)
        fridayRecyclerView.adapter=scheduleAdapter
        fridayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

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
                    arrScheduleFriday.add(ScheduleModel(0,"friday",lectureName,timeName))

                    scheduleAdapter.notifyItemChanged(arrScheduleFriday.size-1)
                    fridayRecyclerView.scrollToPosition(arrScheduleFriday.size-1)
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
        // Handle edit schedule click in Friday fragment
        // You can open a dialog or perform any specific action here
        val editedSchedule = arrScheduleFriday[position]
        val xyz: String = arrScheduleFriday[position].day
        val abc: String = arrScheduleFriday[position].subject
        // Example: Open a dialog with the schedule details for editing
        Toast.makeText(context, xyz + abc, Toast.LENGTH_SHORT).show()
        openEditScheduleDialog(editedSchedule)
    }

    override fun onDeleteScheduleClicked(position: Int) {
        // Handle delete schedule click in Friday fragment
        // You can open a dialog or perform any specific action here
        val deletedSchedule = arrScheduleFriday[position]
        // Example: Open a dialog for confirmation before deleting
        openDeleteScheduleDialog(deletedSchedule)
    }

    private fun openEditScheduleDialog(scheduleModel: ScheduleModel) {
        // Implement the dialog for editing the schedule here
    }

    private fun openDeleteScheduleDialog(scheduleModel: ScheduleModel) {
        // Implement the dialog for confirming deletion here
    }



}