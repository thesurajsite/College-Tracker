package ScheduleFragments

import Activities.sharedPreferenceManager
import AttendanceRoomDatabase.DatabaseHelper
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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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


class SaturdayFragment : Fragment() , ScheduleItemClickListener {
    private val arrScheduleSaturday = ArrayList<ScheduleModel>()
    private lateinit var scheduleAdapter: RecyclerScheduleAdapter
    private lateinit var database: ScheduleDatabaseHelper
    private lateinit var attDatabase: DatabaseHelper //Attendance Database for AutoCompleteTextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_saturday, container, false)

        //Initialization of Database
        database= Room.databaseBuilder(requireContext(),
            ScheduleDatabaseHelper::class.java,
            "ScheduleDB").build()

        //Initialization of Attendance RoomDatabase for AutoCompleteTextView
        attDatabase=Room.databaseBuilder(requireContext(),
            DatabaseHelper::class.java,
            "AttendanceDB").build()

        //val arrScheduleSaturday=ArrayList<ScheduleModel>()
        val floatingActionButton=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


//        arrScheduleSaturday.add(ScheduleModel(0,"saturday","Mathematics","09:00"))
//        arrScheduleSaturday.add(ScheduleModel(0,"saturday","English","10:00"))
//        arrScheduleSaturday.add(ScheduleModel(0,"saturday","VEEES","11:00"))
//        arrScheduleSaturday.add(ScheduleModel(0,"saturday","Thermodynamics","12:00"))
//        arrScheduleSaturday.add(ScheduleModel(0,"saturday","---Saturday---","01:00"))
//        arrScheduleSaturday.add(ScheduleModel(0,"saturday","Geology","02:00"))

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

                    if(day=="saturday") {
                        arrScheduleSaturday.add(ScheduleModel(subjectId, day, lecture, time))
                    }
                }

                scheduleAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //Toast.makeText(context, "hii", Toast.LENGTH_SHORT).show()



        val saturdayRecyclerView = view.findViewById<RecyclerView>(R.id.SaturdayRecyclerView)
        scheduleAdapter= RecyclerScheduleAdapter(requireContext(), this, arrScheduleSaturday)
        saturdayRecyclerView.adapter=scheduleAdapter
        saturdayRecyclerView.layoutManager= LinearLayoutManager(requireContext())

        floatingActionButton.setOnClickListener {
            vibrator.vibrate(50)
            val dialog= Dialog(requireContext())
            dialog.setContentView(R.layout.add_update_schedule)

            val addLecture=dialog.findViewById<AutoCompleteTextView>(R.id.addLecture)
            val addTime=dialog.findViewById<EditText>(R.id.addTime)
            val addSchedule=dialog.findViewById<ImageView>(R.id.addSchedule)
            val deleteSchedule=dialog.findViewById<ImageView>(R.id.deleteSchedule)

            // Creating an array for AutoCompleteTextView
            lifecycleScope.launch {
                val arrSubjectNames=attDatabase.attendanceDao().getAllSubjectNames()
                val actvAdapter= ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrSubjectNames )
                addLecture.setAdapter(actvAdapter)
                addLecture.threshold=1
            }

            addSchedule.setOnClickListener {
                vibrator.vibrate(50)
                var lectureName=""
                var timeName=""

                lectureName=addLecture.text.toString()
                timeName=addTime.text.toString()

                val sharedPreferenceManager= sharedPreferenceManager(requireContext())
                var scheduleID=sharedPreferenceManager.getScheduleID()

                if(lectureName!="")
                {
                    arrScheduleSaturday.add(ScheduleModel(scheduleID,"saturday",lectureName,timeName))
                    scheduleAdapter.notifyItemChanged(arrScheduleSaturday.size-1)
                    saturdayRecyclerView.scrollToPosition(arrScheduleSaturday.size-1)

                    // INSERT DATA INTO THE DATABASE
                    GlobalScope.launch {
                        database.scheduleDao().insertSchedule(
                            ScheduleDataclass(
                                scheduleID,
                                "saturday",
                                lectureName,
                                timeName
                            )
                        )
                    }

                    scheduleID++
                    sharedPreferenceManager.updateScheduleID(scheduleID)

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
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_update_schedule)

        val addLecture = dialog.findViewById<AutoCompleteTextView>(R.id.addLecture)
        val addTime = dialog.findViewById<EditText>(R.id.addTime)
        val addSchedule = dialog.findViewById<ImageView>(R.id.addSchedule)
        val deleteSchedule = dialog.findViewById<ImageView>(R.id.deleteSchedule)
        val id = arrScheduleSaturday[position].subjectId
        val vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(50)

        addLecture.setText(arrScheduleSaturday[position].subject)
        addTime.setText(arrScheduleSaturday[position].time)

        // Creating an array for AutoCompleteTextView
        lifecycleScope.launch {
            val arrSubjectNames=attDatabase.attendanceDao().getAllSubjectNames()
            val actvAdapter=ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrSubjectNames )
            addLecture.setAdapter(actvAdapter)
            addLecture.threshold=1
        }


        //  Toast.makeText(requireContext(), position.toString() + arrScheduleSaturday[position].subject.toString(), Toast.LENGTH_SHORT).show()

        addSchedule.setOnClickListener {
            vibrator.vibrate(50)
            var lectureName = ""
            var timeName = ""

            lectureName = addLecture.text.toString()
            timeName = addTime.text.toString()

            if(id!=0){
                if (lectureName != "") {
                    arrScheduleSaturday.set(position, ScheduleModel(id, "saturday", lectureName, timeName))
                    scheduleAdapter.notifyItemChanged(position)

                    // UPDATING DATABASE
                    var dataToUpdate: ScheduleDataclass = ScheduleDataclass(id, "saturday",lectureName,timeName)
                    lifecycleScope.launch(Dispatchers.IO) {
                        database.scheduleDao().updateSchedule(dataToUpdate)
                    }

                    dialog.dismiss()

                } else {
                    Toast.makeText(context, "Lecture Can't be Empty", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(context, "Please exit Schedule page once before updating this Lecture", Toast.LENGTH_SHORT).show()
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

                        val subjectId=arrScheduleSaturday[position].subjectId
                        //DELETING FROM DATABASE
                        GlobalScope.launch {
                            database.scheduleDao().deleteSchedule(subjectId)
                        }

                        arrScheduleSaturday.removeAt(position)
                        scheduleAdapter.notifyItemRemoved(position)
                        scheduleAdapter.notifyItemRangeChanged(position, arrScheduleSaturday.size - position)
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