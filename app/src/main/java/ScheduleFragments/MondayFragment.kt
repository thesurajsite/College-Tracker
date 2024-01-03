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
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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

        val arrScheduleMonday=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator


        arrScheduleMonday.add(ScheduleModel(1,"monday","Mathematics","09:00"))
        arrScheduleMonday.add(ScheduleModel(1,"monday","English","10:00"))
        arrScheduleMonday.add(ScheduleModel(1,"monday","VEEES","11:00"))
        arrScheduleMonday.add(ScheduleModel(1,"monday","Thermodynamics","12:00"))
        arrScheduleMonday.add(ScheduleModel(1,"monday","---Break---","01:00"))
        arrScheduleMonday.add(ScheduleModel(1,"monday","Geology","02:00"))


        val mondayRecyclerView = view.findViewById<RecyclerView>(R.id.MondayRecyclerView)
        val scheduleAdapter=RecyclerScheduleAdapter(requireContext(), arrScheduleMonday)
        mondayRecyclerView.adapter=scheduleAdapter
        mondayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

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
                    arrScheduleMonday.add(ScheduleModel(0,"monday",lectureName,timeName))

                    scheduleAdapter.notifyItemChanged(arrScheduleMonday.size-1)
                    mondayRecyclerView.scrollToPosition(arrScheduleMonday.size-1)
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