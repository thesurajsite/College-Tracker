package com.example.collegetracker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val arrAttendance=ArrayList<AttendenceModel>()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val floatingActionButton=findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val adapter = RecyclerAttendanceAdapter(this, arrAttendance)


        floatingActionButton.setOnClickListener {
          // Toast.makeText(this, "hii", Toast.LENGTH_SHORT).show()

            val dialog=Dialog(this)
            dialog.setContentView(R.layout.add_update_layout)

            // addSubject FROM THE add_update_layout
            val addSubject=dialog.findViewById<EditText>(R.id.addSubject)
            val addConducted=dialog.findViewById<EditText>(R.id.addConducted)
            val addAttended=dialog.findViewById<EditText>(R.id.addAttended)
            val addButton=dialog.findViewById<Button>(R.id.addButton)
            val minusConducted=dialog.findViewById<ImageView>(R.id.minusConducted)
            val plusConducted=dialog.findViewById<ImageView>(R.id.plusConducted)
            val minusAttended=dialog.findViewById<ImageView>(R.id.minusAttended)
            val plusAttended=dialog.findViewById<ImageView>(R.id.plusAttended)

            // subjectName AND SIMILAR STORES THE STRING VALUES THAT WE GET FROM THE add_update_layout
            var subjectName: String=""
            var conductedName: String="0"
            var attendedName: String="0"
            var percentageName: Int=0;
            var percentageString: String=""


            var tempConducted=conductedName.toInt()
            var tempAttended=attendedName.toInt()

            // PLUS MINUS BUTTON CONTROLS
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



            //ADD BUTTON ON CLICK LISTENER
            addButton.setOnClickListener {


                // subjectName AND SIMILAR STORES THE STRING VALUES THAT WE GET FROM THE add_update_layout
                // AND FINALLY DISPLAYS IN THE RECYCLER VIEW
                subjectName=addSubject.text.toString()
                conductedName=addConducted.text.toString()
                attendedName=addAttended.text.toString()
                //val numericValue: Double = conductedName.toDouble()


                if(subjectName!="")
                {

                    if(conductedName.isEmpty())
                        conductedName="0"
                    if(attendedName.isEmpty())
                        attendedName="0"

                    // PERCENATGE CALCULATION
                    percentageName=((attendedName.toDouble()/conductedName.toDouble())*100).toInt()
                    percentageString=percentageName.toString()+"%"


                    //Passing data to Attendence Array
                    arrAttendance.add(AttendenceModel(percentageString,subjectName,conductedName,attendedName))
                    adapter.notifyItemChanged(arrAttendance.size-1)
                    recyclerView.scrollToPosition(arrAttendance.size-1)

                    dialog.dismiss()

                }
                else{
                    Toast.makeText(this, "Subject can't be Empty", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

            dialog.show()



        }






        arrAttendance.add(AttendenceModel("99%","Physics","30","25"))
        arrAttendance.add(AttendenceModel("78%","Chemistry","20","15"))
        arrAttendance.add(AttendenceModel("36%","Biology","25","20"))
        arrAttendance.add(AttendenceModel("48%","Math","16","12"))

        recyclerView.layoutManager=LinearLayoutManager(this)
        val recyclerAdapter =RecyclerAttendanceAdapter(this, arrAttendance)
        recyclerView.adapter=recyclerAdapter




    }
}