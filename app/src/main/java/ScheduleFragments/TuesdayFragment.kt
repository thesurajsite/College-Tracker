package ScheduleFragments

import ScheduleRecyclerView.RecyclerScheduleAdapter
import ScheduleRecyclerView.ScheduleItemClickListener
import ScheduleRecyclerView.ScheduleModel
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TuesdayFragment : Fragment(), ScheduleItemClickListener {

    private val arrScheduleTuesday = ArrayList<ScheduleModel>()
    private lateinit var scheduleAdapter: RecyclerScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tuesday, container, false)

      //  val arrScheduleTuesday = ArrayList<ScheduleModel>()
        val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        arrScheduleTuesday.add(ScheduleModel(0, "tuesday", "Mathematics", "09:00"))
        arrScheduleTuesday.add(ScheduleModel(0, "tuesday", "English", "10:00"))
        arrScheduleTuesday.add(ScheduleModel(0, "tuesday", "VEEES", "11:00"))
        arrScheduleTuesday.add(ScheduleModel(0, "tuesday", "Thermodynamics", "12:00"))
        arrScheduleTuesday.add(ScheduleModel(0, "tuesday", "---Tuesday---", "01:00"))
        arrScheduleTuesday.add(ScheduleModel(0, "tuesday", "Geology", "02:00"))

        val tuesdayRecyclerView = view.findViewById<RecyclerView>(R.id.TuesdayRecyclerView)
        scheduleAdapter = RecyclerScheduleAdapter(requireContext(), this, arrScheduleTuesday)
        tuesdayRecyclerView.adapter = scheduleAdapter
        tuesdayRecyclerView.layoutManager = LinearLayoutManager(requireContext())

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
                    arrScheduleTuesday.add(ScheduleModel(0, "tuesday", lectureName, timeName))

                    scheduleAdapter.notifyItemChanged(arrScheduleTuesday.size - 1)
                    tuesdayRecyclerView.scrollToPosition(arrScheduleTuesday.size - 1)
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
        val id = arrScheduleTuesday[position].subjectId
        val vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(50)

        addLecture.setText(arrScheduleTuesday[position].subject)
        addTime.setText(arrScheduleTuesday[position].time)

        Toast.makeText(requireContext(), position.toString() + arrScheduleTuesday[position].subject.toString(), Toast.LENGTH_SHORT).show()

        addSchedule.setOnClickListener {
            vibrator.vibrate(50)
            var lectureName = ""
            var timeName = ""

            lectureName = addLecture.text.toString()
            timeName = addTime.text.toString()

            if (lectureName != "") {
                arrScheduleTuesday.set(position, ScheduleModel(id, "tuesday", lectureName, timeName))
                scheduleAdapter.notifyItemChanged(position)

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
                        arrScheduleTuesday.removeAt(position)
                        scheduleAdapter.notifyItemRemoved(position)
                        scheduleAdapter.notifyItemRangeChanged(position, arrScheduleTuesday.size - position)
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
