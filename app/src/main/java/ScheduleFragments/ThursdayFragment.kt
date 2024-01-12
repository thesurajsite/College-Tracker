package ScheduleFragments

import AttendanceRoomDatabase.Attendance
import AttendanceRoomDatabase.DatabaseHelper
import RecyclerView.AttendenceModel
import ScheduleRecyclerView.RecyclerScheduleAdapter
import ScheduleRecyclerView.ScheduleItemClickListener
import ScheduleRecyclerView.ScheduleModel
import ScheduleRoomDatabase.ScheduleDatabaseHelper
import ScheduleRoomDatabase.ScheduleDataclass
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Bundle
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.collegetracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThursdayFragment : Fragment(), ScheduleItemClickListener {
    private val arrScheduleThursday = ArrayList<ScheduleModel>()
    private lateinit var scheduleAdapter: RecyclerScheduleAdapter
    private lateinit var database: ScheduleDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_thursday, container, false)

        //Initialization of Database
        database= Room.databaseBuilder(requireContext(),
            ScheduleDatabaseHelper::class.java,
            "ScheduleDB").build()

        //val arrScheduleThursday = ArrayList<ScheduleModel>()
        val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

//        // Clearing the RecyclerView Array and Re-Poulating it with the DataBase
//        arrScheduleThursday.add(ScheduleModel(0, "thursday", "Mathematics", "09:00"))
//        arrScheduleThursday.add(ScheduleModel(0, "thursday", "English", "10:00"))
//        arrScheduleThursday.add(ScheduleModel(0, "thursday", "VEEES", "11:00"))
//        arrScheduleThursday.add(ScheduleModel(0, "thursday", "Thermodynamics", "12:00"))
//        arrScheduleThursday.add(ScheduleModel(0, "thursday", "---Thursday---", "01:00"))
//        arrScheduleThursday.add(ScheduleModel(0, "thursday", "Geology", "02:00"))


        lifecycleScope.launch {
            try {
                val scheduleList = withContext(Dispatchers.IO) {
                    database.scheduleDao().getAllSchedule()
                        }

                for (schedule in scheduleList) {
                    val subjectId = schedule.id
                    val day = schedule.day
                    val lecture = schedule.lecture
                    val time = schedule.time

                    if(day=="thursday") {
                        arrScheduleThursday.add(ScheduleModel(subjectId, day, lecture, time))
                    }
                }

                scheduleAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //Toast.makeText(context, "hii", Toast.LENGTH_SHORT).show()




        val thursdayRecyclerView = view.findViewById<RecyclerView>(R.id.ThursdayRecyclerView)
        scheduleAdapter = RecyclerScheduleAdapter(requireContext(), this, arrScheduleThursday)
        thursdayRecyclerView.adapter = scheduleAdapter
        thursdayRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        floatingActionButton.setOnClickListener {
            vibrator.vibrate(50)
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.add_update_schedule)

            val addLecture = dialog.findViewById<EditText>(R.id.addLecture)
            val addTime = dialog.findViewById<EditText>(R.id.addTime)
            val addSchedule = dialog.findViewById<ImageView>(R.id.addSchedule)
            val deleteSchedule = dialog.findViewById<ImageView>(R.id.deleteSchedule)

            addSchedule.setOnClickListener {
                vibrator.vibrate(50)
                var lectureName = ""
                var timeName = ""

                lectureName = addLecture.text.toString()
                timeName = addTime.text.toString()

                if (lectureName != "") {
                    arrScheduleThursday.add(ScheduleModel(0, "thursday", lectureName, timeName))
                    scheduleAdapter.notifyItemChanged(arrScheduleThursday.size - 1)
                    thursdayRecyclerView.scrollToPosition(arrScheduleThursday.size - 1)

                    // INSERT DATA INTO THE DATABASE
                    GlobalScope.launch {
                        database.scheduleDao().insertSchedule(
                            ScheduleDataclass(
                                0,
                                "thursday",
                                lectureName,
                                timeName
                            )
                        )
                    }

                    dialog.dismiss()
                } else {
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
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_update_schedule)

        val addLecture = dialog.findViewById<EditText>(R.id.addLecture)
        val addTime = dialog.findViewById<EditText>(R.id.addTime)
        val addSchedule = dialog.findViewById<ImageView>(R.id.addSchedule)
        val deleteSchedule = dialog.findViewById<ImageView>(R.id.deleteSchedule)
        val id = arrScheduleThursday[position].subjectId
        val vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(50)

        addLecture.setText(arrScheduleThursday[position].subject)
        addTime.setText(arrScheduleThursday[position].time)

        //Toast.makeText(requireContext(), position.toString() + arrScheduleThursday[position].subject.toString(), Toast.LENGTH_SHORT).show()

        addSchedule.setOnClickListener {
            vibrator.vibrate(50)
            var lectureName = ""
            var timeName = ""

            lectureName = addLecture.text.toString()
            timeName = addTime.text.toString()

            if (lectureName != "") {
                arrScheduleThursday.set(position, ScheduleModel(id, "thursday", lectureName, timeName))
                scheduleAdapter.notifyItemChanged(position)

                // UPDATING DATABASE
                var dataToUpdate: ScheduleDataclass = ScheduleDataclass(id, "thursday",lectureName,timeName)
                lifecycleScope.launch(Dispatchers.IO) {
                    database.scheduleDao().updateSchedule(dataToUpdate)
                }

                dialog.dismiss()

            } else {
                Toast.makeText(context, "Lecture Can't be Empty", Toast.LENGTH_SHORT).show()
            }
        }

        deleteSchedule.setOnClickListener(View.OnClickListener {
            vibrator.vibrate(50)

            val builder = AlertDialog.Builder(context)
                .setTitle("Delete Lecture")
                .setIcon(R.drawable.baseline_delete_24)
                .setMessage("Do you want to Delete this Lecture ?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->
                    try {

                        vibrator.vibrate(50)

                        val subjectId=arrScheduleThursday[position].subjectId
                        //DELETING FROM DATABASE
                        GlobalScope.launch {
                            database.scheduleDao().deleteSchedule(subjectId)
                        }

                        arrScheduleThursday.removeAt(position)
                        scheduleAdapter.notifyItemRemoved(position)
                        scheduleAdapter.notifyItemRangeChanged(position, arrScheduleThursday.size - position)
                        dialog.dismiss()

                    } catch (e: Exception) {
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
                .setNegativeButton(
                    "No"
                ) { dialogInterface, i ->
                    vibrator.vibrate(50)
                    Toast.makeText(context, "Deletion Cancelled", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            builder.show()
        })

        dialog.show()

    }


}

