package com.example.collegetracker


import android.app.Dialog
import android.content.Context
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
import kotlin.collections.ArrayList

class RecyclerAttendanceAdapter(val context: Context,val arrAttendance: ArrayList<AttendenceModel>) : RecyclerView.Adapter<RecyclerAttendanceAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        // subject, conductNumber, attendNumber (FROM THE attendance_row LAYOUT)
        val percentage=itemView.findViewById<TextView>(R.id.percentage)
        val subject = itemView.findViewById<TextView>(R.id.subject)
        val conductNumber=itemView.findViewById<TextView>(R.id.conductNumber)
        val attendNumber=itemView.findViewById<TextView>(R.id.attendNumber)

        val conductMinus=itemView.findViewById<ImageView>(R.id.conductMinus)
        val conductPlus=itemView.findViewById<ImageView>(R.id.conductPlus)
        val attendMinus=itemView.findViewById<ImageView>(R.id.attendMinus)
        val attendPlus=itemView.findViewById<ImageView>(R.id.attendPlus)

        val recyclerLayout=itemView.findViewById<LinearLayout>(R.id.recyclerLayout)









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


        holder.recyclerLayout.setOnLongClickListener {
           // Toast.makeText(context, "hiii", Toast.LENGTH_SHORT).show()

            val dialog= Dialog(context)
            dialog.setContentView(R.layout.add_update_layout)

            val addSubject=dialog.findViewById<EditText>(R.id.addSubject)
            val addConducted=dialog.findViewById<EditText>(R.id.addConducted)
            val addAttended=dialog.findViewById<EditText>(R.id.addAttended)
            val addSubjectTitle=dialog.findViewById<TextView>(R.id.addSubjectTitle)
            val addButton=dialog.findViewById<Button>(R.id.addButton)
            val minusConducted=dialog.findViewById<ImageView>(R.id.minusConducted)
            val plusConducted=dialog.findViewById<ImageView>(R.id.plusConducted)
            val minusAttended=dialog.findViewById<ImageView>(R.id.minusAttended)
            val plusAttended=dialog.findViewById<ImageView>(R.id.plusAttended)


            addSubject.setText(arrAttendance[position].subject)
            addConducted.setText(arrAttendance[position].conducted)
            addAttended.setText(arrAttendance[position].attended)
            addSubjectTitle.setText("Update Subject")
            addButton.setText("Update")

            var tempConducted=arrAttendance[position].conducted.toInt()
            var tempAttended=arrAttendance[position].attended.toInt()

            minusConducted.setOnClickListener {
                tempConducted--
                addConducted.setText(tempConducted.toString())
            }

            plusConducted.setOnClickListener {
                tempConducted++
                addConducted.setText(tempConducted.toString())
            }

            minusAttended.setOnClickListener {
                tempAttended--
                addAttended.setText(tempAttended.toString())
            }

            plusAttended.setOnClickListener {
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

                subjectName=addSubject.text.toString()
                conductedName=addConducted.text.toString()
                attendedName=addAttended.text.toString()

                if(subjectName!="")
                {

                    // PERCENATGE CALCULATION
                    percentageName=((attendedName.toDouble()/conductedName.toDouble())*100).toInt()
                    percentageString=percentageName.toString()+"%"


                    //Passing data to Attendence Array
                    arrAttendance.set(position, AttendenceModel(percentageString,subjectName,conductedName,attendedName))
                    notifyItemChanged(position)

                    dialog.dismiss()

                }
                else{
                    Toast.makeText(context, "Subject can't be Empty", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }


            }


            dialog.show()
            true

        }





    }




}
