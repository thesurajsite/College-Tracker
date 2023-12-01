package com.example.collegetracker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

            val addSubject=dialog.findViewById<EditText>(R.id.addSubject)
            val addConducted=dialog.findViewById<EditText>(R.id.addConducted)
            val addAttended=dialog.findViewById<EditText>(R.id.addAttended)
            val addButton=dialog.findViewById<Button>(R.id.addButton)

            addButton.setOnClickListener {
                var subjectName: String=""
                var conductedName: String=""
                var attendedName: String=""


                subjectName=addSubject.text.toString()
                conductedName=addConducted.text.toString()
                attendedName=addAttended.text.toString()

                if(subjectName!="")
                {
                    if(conductedName=="")
                        conductedName="0"
                    if(attendedName=="")
                        attendedName="0"

                    arrAttendance.add(AttendenceModel(R.drawable.bulleticon,subjectName,conductedName,attendedName))
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



        arrAttendance.add(AttendenceModel(R.drawable.bulleticon,"Physics","30","25"))
        arrAttendance.add(AttendenceModel(R.drawable.bulleticon,"Chemistry","20","15"))
        arrAttendance.add(AttendenceModel(R.drawable.bulleticon,"Biology","25","20"))
        arrAttendance.add(AttendenceModel(R.drawable.bulleticon,"Math","16","12"))

        recyclerView.layoutManager=LinearLayoutManager(this)
        val recyclerAdapter =RecyclerAttendanceAdapter(this, arrAttendance)
        recyclerView.adapter=recyclerAdapter




    }
}