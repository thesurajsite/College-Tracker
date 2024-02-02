package RecyclerView


import Activities.MainActivity
import AttendanceRoomDatabase.Attendance
import AttendanceRoomDatabase.DatabaseHelper
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.collegetracker.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecyclerAttendanceAdapter(val context: Context,val arrAttendance: ArrayList<AttendenceModel>) : RecyclerView.Adapter<RecyclerAttendanceAdapter.ViewHolder>() {


    //Initialization of Database
    private var database: DatabaseHelper
    init {
        // Initialize the database using the context
        database = Room.databaseBuilder(
            context.applicationContext,
            DatabaseHelper::class.java,
            "AttendanceDB"
        ).build()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){



        // subject, conductNumber, attendNumber (FROM THE attendance_row LAYOUT)
        val percentage=itemView.findViewById<TextView>(R.id.percentage)
        val subject = itemView.findViewById<TextView>(R.id.subject)
        val conductNumber=itemView.findViewById<TextView>(R.id.conductNumber)
        val attendNumber=itemView.findViewById<TextView>(R.id.attendNumber)

//        val conductMinus=itemView.findViewById<ImageView>(R.id.conductMinus)
//        val conductPlus=itemView.findViewById<ImageView>(R.id.conductPlus)
//        val attendMinus=itemView.findViewById<ImageView>(R.id.attendMinus)
//        val attendPlus=itemView.findViewById<ImageView>(R.id.attendPlus)

        val recyclerLayout=itemView.findViewById<LinearLayout>(R.id.recyclerLayout)
        //VIBRATOR VIBRATOR VIBRATOR
        val vibrator = itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.attendance_row, parent, false))

    }

    override fun getItemCount(): Int {
        return arrAttendance.size


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.percentage.text=arrAttendance[position].percentage
        holder.subject.text=arrAttendance[position].subject
        holder.attendNumber.text=arrAttendance[position].attended
        holder.conductNumber.text=arrAttendance[position].conducted


        holder.recyclerLayout.setOnClickListener{
            // Toast.makeText(context, "hiii", Toast.LENGTH_SHORT).show()
            holder.vibrator.vibrate(50)


            val dialog= Dialog(context)
            dialog.setContentView(R.layout.add_update_layout)

            val idTextView=dialog.findViewById<TextView>(R.id.id)
            val addSubject=dialog.findViewById<EditText>(R.id.addSubject)
            val addConducted=dialog.findViewById<EditText>(R.id.addConducted)
            val addAttended=dialog.findViewById<EditText>(R.id.addAttended)
            val addSubjectTitle=dialog.findViewById<TextView>(R.id.addSubjectTitle)
            val addButton=dialog.findViewById<Button>(R.id.addButton)
            val minusConducted=dialog.findViewById<ImageView>(R.id.minusConducted)
            val plusConducted=dialog.findViewById<ImageView>(R.id.plusConducted)
            val minusAttended=dialog.findViewById<ImageView>(R.id.minusAttended)
            val plusAttended=dialog.findViewById<ImageView>(R.id.plusAttended)
            val deleteButton=dialog.findViewById<ImageView>(R.id.deleteButton)
            val lastUpdated=dialog.findViewById<TextView>(R.id.lastUpdated)
            val id=arrAttendance[position].subjectId
            val lastUpdated_String=arrAttendance[position].lastUpdated


            lastUpdated.setText("Last Updated $lastUpdated_String")
            idTextView.setText("ID: "+arrAttendance[position].subjectId.toString())
            addSubject.setText(arrAttendance[position].subject)
            addConducted.setText(arrAttendance[position].conducted)
            addAttended.setText(arrAttendance[position].attended)
            addSubjectTitle.setText("Update Subject")
            addButton.setText("Update")

            var tempConducted=arrAttendance[position].conducted.toInt()
            var tempAttended=arrAttendance[position].attended.toInt()

            minusConducted.setOnClickListener {
                holder.vibrator.vibrate(50)
                tempConducted--
                addConducted.setText(tempConducted.toString())
            }

            plusConducted.setOnClickListener {
                holder.vibrator.vibrate(50)
                tempConducted++
                addConducted.setText(tempConducted.toString())
            }

            minusAttended.setOnClickListener {
                holder.vibrator.vibrate(50)
                tempAttended--
                addAttended.setText(tempAttended.toString())
            }

            plusAttended.setOnClickListener {
                holder.vibrator.vibrate(50)
                tempAttended++
                addAttended.setText(tempAttended.toString())
            }

            // subjectName AND SIMILAR STORES THE STRING VALUES THAT WE GET FROM THE add_update_layout
            var subjectName: String=""
            var conductedName: String="0"
            var attendedName: String="0"
            var percentageName: Int=0;
            var percentageString: String=""

            addButton.setOnClickListener {
                holder.vibrator.vibrate(50)

                if(id!=0)
                {
                    subjectName=addSubject.text.toString()
                    conductedName=addConducted.text.toString()
                    attendedName=addAttended.text.toString()

                    if(subjectName!="")
                    {

                        val subjectId: Int =arrAttendance[position].subjectId
                        // PERCENATGE CALCULATION
                        percentageName=((attendedName.toDouble()/conductedName.toDouble())*100).toInt()
                        percentageString=percentageName.toString()+"%"

                        val currentTime=currentTime().toString()

                        // Passing data to Attendence Array
                        arrAttendance.set(position,
                            AttendenceModel(subjectId,percentageString,subjectName,conductedName,attendedName,currentTime)
                        )

                        notifyItemChanged(position)


                        var dataToUpdate: Attendance = Attendance(
                            id=arrAttendance[position].subjectId,
                            percentage = arrAttendance[position].percentage,
                            subjectName = arrAttendance[position].subject,
                            classesConducted = arrAttendance[position].conducted,
                            classesAttended = arrAttendance[position].attended,
                            lastUpdated = arrAttendance[position].lastUpdated
                        )


                        GlobalScope.launch {
                            database.attendanceDao().updateAttendance(dataToUpdate)
                        }

                        dialog.dismiss()
                    }
                    else{
                        Toast.makeText(context, "Subject can't be Empty", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }


                }
                else{
                    Toast.makeText(context, "Please close the app once before making any update to this Subject", Toast.LENGTH_SHORT).show()
                }



            }



            deleteButton.setOnClickListener(View.OnClickListener {

                holder.vibrator.vibrate(50)
                val builder = AlertDialog.Builder(context)
                    .setTitle("Delete Subject")
                    .setIcon(R.drawable.baseline_delete_24)
                    .setMessage("Do you want to Delete this Subject ?")
                    .setPositiveButton(
                        "Yes"
                    ) { dialogInterface, i ->
                        try {
                            val subjectId: Int =arrAttendance[position].subjectId
                            arrAttendance.removeAt(position)
                            notifyItemRemoved(position)

                            //DELETING FROM DATABASE
                            GlobalScope.launch {
                                database.attendanceDao().deleteAttendance(subjectId)
                            }

                            dialog.dismiss()


                        } catch (e: Exception) {
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                            Log.w("crash-attendance", e)
                            dialog.dismiss()
                        }

                    }
                    .setNegativeButton(
                        "No"
                    ) { dialogInterface, i ->
                        Toast.makeText(context, "Deletion Cancelled", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                builder.show()
            })

            dialog.show()
            true

        }
    }

    private fun currentTime(): String? {
        val currentDateTime = LocalDateTime.now()

        // Format the date and time with the desired pattern (dd-MM HH:mm)
        val formatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm")
        val DateTime = currentDateTime.format(formatter).toString()

        return DateTime
    }

}