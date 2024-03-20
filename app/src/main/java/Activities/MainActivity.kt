package Activities

import AttendanceRoomDatabase.Attendance
import AttendanceRoomDatabase.DatabaseHelper
import RecyclerView.AttendenceModel
import RecyclerView.RecyclerAttendanceAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.startUpdateFlowForResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {

    val UPDATE_CODE=1233

    private lateinit var database: DatabaseHelper
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType= AppUpdateType.FLEXIBLE
    private lateinit var arrAttendance:ArrayList<AttendenceModel>
    private lateinit var sharedPreferenceManager:sharedPreferenceManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:RecyclerAttendanceAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appUpdateManager=AppUpdateManagerFactory.create(applicationContext)
        if(updateType==AppUpdateType.FLEXIBLE){
            appUpdateManager.registerListener(installStateUpdateListener)
        }

        checkForAppUpdate()

//        //Initialization of Database
        database = DatabaseHelper.getDB(applicationContext) ?: throw IllegalStateException("Unable to create database instance")
        sharedPreferenceManager=sharedPreferenceManager(this)



        arrAttendance=ArrayList()
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = RecyclerAttendanceAdapter(this, arrAttendance)
        recyclerView.adapter = adapter
        val floatingActionButton=findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val nothingToShowImage: ImageView = findViewById(R.id.nothingToShow)
        val scheduleButton=findViewById<ImageView>(R.id.scheduleButton)
        val helpButton=findViewById<ImageView>(R.id.helpButton)
        val howToUse=findViewById<Button>(R.id.howToUse)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        recyclerView.layoutManager=LinearLayoutManager(this)

        //SHARED PREFERENCE FOR NEW USER, EXECUTES ONE TIME ONLY
        sharedPreferenceForNewUser()
        adapter.notifyItemChanged(arrAttendance.size-1)
        adapter.notifyDataSetChanged()
        refresh()




        // Clearing the RecyclerView Array and Re-Populating it with the DataBase
        GlobalScope.launch {
            arrAttendance.clear()
            var attendanceList=database.attendanceDao().getAllAttendance()

            // Data entered in arrAttendance must be of the type: AttendanceModel
            for (attendance in attendanceList) {  // line 46
                val subjectId=attendance.id
                val percentageString = attendance.percentage
                val subjectName = attendance.subjectName
                val conductedName = attendance.classesConducted
                val attendedName = attendance.classesAttended
                val lastUpdated=attendance.lastUpdated

                arrAttendance.add(AttendenceModel(subjectId, percentageString, subjectName, conductedName, attendedName, lastUpdated))
            }


            // VISIBILITY CONTROL FOR nothingToShow IMAGE
            if (arrAttendance.isEmpty()) {
                recyclerView.visibility = View.GONE
                nothingToShowImage.visibility = View.VISIBLE
                howToUse.visibility=View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                nothingToShowImage.visibility = View.GONE
                howToUse.visibility=View.GONE
            }

        }




        floatingActionButton.setOnClickListener {
            // Toast.makeText(this, "hii", Toast.LENGTH_SHORT).show()

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.add_update_layout)

            // addSubject FROM THE add_update_layout
            val addSubject = dialog.findViewById<EditText>(R.id.addSubject)
            val addConducted = dialog.findViewById<EditText>(R.id.addConducted)
            val addAttended = dialog.findViewById<EditText>(R.id.addAttended)
            val addButton = dialog.findViewById<Button>(R.id.addButton)
            val minusConducted = dialog.findViewById<ImageView>(R.id.minusConducted)
            val plusConducted = dialog.findViewById<ImageView>(R.id.plusConducted)
            val minusAttended = dialog.findViewById<ImageView>(R.id.minusAttended)
            val plusAttended = dialog.findViewById<ImageView>(R.id.plusAttended)
            val deleteButton = dialog.findViewById<ImageView>(R.id.deleteButton)
            val idTextView=dialog.findViewById<TextView>(R.id.id)
            val vibrator = dialog.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            deleteButton.visibility=View.INVISIBLE
            vibrator.vibrate(50)

            // subjectName AND SIMILAR STORES THE STRING VALUES THAT WE GET FROM THE add_update_layout
            var subjectName: String = ""
            var conductedName: String = "0"
            var attendedName: String = "0"
            var percentageName: Int = 0;
            var percentageString: String = ""


            var tempConducted = conductedName.toInt()
            var tempAttended = attendedName.toInt()

            // PLUS MINUS BUTTON CONTROLS
            minusConducted.setOnClickListener {
                vibrator.vibrate(50)
                tempConducted--
                addConducted.setText(tempConducted.toString())
            }
            plusConducted.setOnClickListener {
                vibrator.vibrate(50)
                tempConducted++
                addConducted.setText(tempConducted.toString())
            }

            minusAttended.setOnClickListener {
                vibrator.vibrate(50)
                tempAttended--
                addAttended.setText(tempAttended.toString())
            }

            plusAttended.setOnClickListener {
                vibrator.vibrate(50)
                tempAttended++
                addAttended.setText(tempAttended.toString())
            }


            //ADD BUTTON ON CLICK LISTENER
            addButton.setOnClickListener {


                vibrator.vibrate(50)
                // subjectName AND SIMILAR STORES THE STRING VALUES THAT WE GET FROM THE add_update_layout
                // AND FINALLY DISPLAYS IN THE RECYCLER VIEW
                subjectName = addSubject.text.toString()
                conductedName = addConducted.text.toString()
                attendedName = addAttended.text.toString()
                //val numericValue: Double = conductedName.toDouble()


                if (subjectName != "") {

                    if (conductedName.isEmpty())
                        conductedName = "0"
                    if (attendedName.isEmpty())
                        attendedName = "0"

                    // PERCENATGE CALCULATION
                    percentageName =
                        ((attendedName.toDouble() / conductedName.toDouble()) * 100).toInt()
                    percentageString = percentageName.toString() + "%"

                    val currentTime=currentTime().toString()

                    var attendanceID=sharedPreferenceManager.getAttendanceID()

                    // Adding data to the Database
                    GlobalScope.launch {
                        database.attendanceDao().insertAttendance(Attendance(attendanceID, percentageString, subjectName, conductedName, attendedName, currentTime))
                    }


                    //Passing data to Attendence Array
                    arrAttendance.add(AttendenceModel(attendanceID, percentageString, subjectName, conductedName, attendedName, currentTime))

                    adapter.notifyItemChanged(arrAttendance.size - 1)
                    recyclerView.scrollToPosition(arrAttendance.size - 1)
//                    Toast.makeText(this, attendanceID.toString(), Toast.LENGTH_SHORT).show()

                    idTextView.setText(attendanceID.toString())
                    attendanceID++
                    sharedPreferenceManager.updateAttendanceID(attendanceID)

                    dialog.dismiss()

                    // VISIBILITY CONTROL FOR nothingToShow IMAGE
                    if (arrAttendance.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        nothingToShowImage.visibility = View.VISIBLE
                        howToUse.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        nothingToShowImage.visibility = View.GONE
                        howToUse.visibility = View.GONE
                    }

                } else {
                    Toast.makeText(this, "Subject can't be Empty", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

            deleteButton.setOnClickListener {
                vibrator.vibrate(50)
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            dialog.show()


        }

//        arrAttendance.add(AttendenceModel("83%","Physics","30","25"))
//        arrAttendance.add(AttendenceModel("75%","Chemistry","20","15"))
//        arrAttendance.add(AttendenceModel("80%","Biology","25","20"))
//        arrAttendance.add(AttendenceModel("75%","Math","16","12"))



//        scheduleButton.setOnClickListener {
//            vibrator.vibrate(50)
//            val intent=Intent(this, DailySchedule::class.java)
//            startActivity(intent)
//        }

        scheduleButton.setOnClickListener {
            vibrator.vibrate(50)
            val intent=Intent(this,Daily_Schedule::class.java)
            startActivity(intent)
        }


        helpButton.setOnClickListener {
            vibrator.vibrate(50)
            val intent=Intent(this, instructionsPage::class.java)
            startActivity(intent)
        }

        howToUse.setOnClickListener {
            vibrator.vibrate(50)
            val intent=Intent(this, instructionsPage::class.java)
            startActivity(intent)
        }
    }

    private val installStateUpdateListener=InstallStateUpdatedListener{ state->
        if(state.installStatus()==InstallStatus.DOWNLOADED){
            Toast.makeText(applicationContext, "Update Successful, Restarting in 5 Seconds", Toast.LENGTH_LONG).show()
        }
        lifecycleScope.launch {
            delay(5.seconds)
            appUpdateManager.completeUpdate()
        }
    }

    private fun checkForAppUpdate(){
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info->
            val isUpdateAvailable=info.updateAvailability()==UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed=when(updateType){
                AppUpdateType.FLEXIBLE->info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE->info.isImmediateUpdateAllowed
                else-> false
            }

            if(isUpdateAvailable && isUpdateAllowed){
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    this,
                    UPDATE_CODE
                )
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if(updateType==AppUpdateType.IMMEDIATE){
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info->
                if(info.updateAvailability()==UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateType,
                        this,
                        UPDATE_CODE
                    )
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==UPDATE_CODE){
            if(resultCode!= RESULT_OK){
                println("Somerthing went wrong while updating...")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(updateType==AppUpdateType.FLEXIBLE){
            appUpdateManager.unregisterListener(installStateUpdateListener)
        }

    }

    private fun currentTime(): String? {
        val currentDateTime = LocalDateTime.now()

        // Format the date and time with the desired pattern (dd-MM HH:mm)
        val formatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm")
        val DateTime = currentDateTime.format(formatter).toString()

        return DateTime
    }

    private fun sharedPreferenceForNewUser(){

        // SharedPreference for new User
        if(sharedPreferenceManager.getUserVersion()==0){
            // Adding data to the Database
            val currentTime=currentTime().toString()

            arrAttendance.add(AttendenceModel(1, "100%", "Subject 1", "10", "10", currentTime))
            arrAttendance.add(AttendenceModel(2, "60%", "Subject 2", "10", "6", currentTime))
            arrAttendance.add(AttendenceModel(3, "40%", "Subject 3", "10", "4", currentTime))

            GlobalScope.launch {
                database.attendanceDao().insertAttendance(Attendance(1, "100%", "Subject 1", "10", "10", currentTime))
                database.attendanceDao().insertAttendance(Attendance(2, "60%", "Subject 2", "10", "6", currentTime))
                database.attendanceDao().insertAttendance(Attendance(3, "40%", "Subject 3", "10", "4", currentTime))


            }

            adapter.notifyItemChanged(arrAttendance.size-1)
            sharedPreferenceManager.updateUserVersion(1)


        }


    }

    private fun refresh(){
        adapter.notifyItemChanged(arrAttendance.size-1)
    }
}