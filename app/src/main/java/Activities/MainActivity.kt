package Activities

import Models.Attendance
import Database.DatabaseHelper
import Models.AttendenceModel
import Adapters.RecyclerAttendanceAdapter
import Models.AttendanceViewModel
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R
import com.collegetracker.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {

    val UPDATE_CODE=1233

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseHelper
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType= AppUpdateType.FLEXIBLE
    private lateinit var arrAttendance:ArrayList<Attendance>
    private lateinit var sharedPreferenceManager:sharedPreferenceManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerAttendanceAdapter
    lateinit var viewModel: AttendanceViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appUpdateManager=AppUpdateManagerFactory.create(applicationContext)
        if(updateType==AppUpdateType.FLEXIBLE){
            appUpdateManager.registerListener(installStateUpdateListener)
        }

        checkForAppUpdate()

//        //Initialization of Database
        database = DatabaseHelper.getDB(applicationContext) ?: throw IllegalStateException("Unable to create database instance")
        sharedPreferenceManager=sharedPreferenceManager(this)

        viewModel=ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as Application))
            .get(AttendanceViewModel::class.java)

        arrAttendance=ArrayList()
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = RecyclerAttendanceAdapter(this, arrAttendance, viewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager=LinearLayoutManager(this)
        val floatingActionButton=findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val nothingToShowImage: ImageView = findViewById(R.id.nothingToShow)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator




        //SHARED PREFERENCE FOR NEW USER, EXECUTES ONE TIME ONLY
        sharedPreferenceForNewUser()
        adapter.notifyItemChanged(arrAttendance.size-1)
        adapter.notifyDataSetChanged()
        refresh()

        // POPULATE THE RECYCLERVIEW ON ACTIVITY STARTUP
        viewModel.allAttendance.observe(this){ list->
            list?.let {
                arrAttendance.clear()
                arrAttendance.addAll(list)
                adapter.notifyDataSetChanged()

                //  VISIBILITY CONTROL FOR nothingToShow IMAGE
                if (arrAttendance.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    nothingToShowImage.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    nothingToShowImage.visibility = View.GONE
                }
            }

        }


        floatingActionButton.setOnClickListener {
            vibrator.vibrate(50)

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
                        database.attendanceDao().insertAttendance(Attendance(attendanceID, percentageString, subjectName, conductedName, attendedName, currentTime, "75%"))
                    }


                    //Passing data to Attendence Array
                    arrAttendance.add(Attendance(attendanceID, percentageString, subjectName, conductedName, attendedName, currentTime, "75%"))

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
//                        howToUse.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        nothingToShowImage.visibility = View.GONE
//                        howToUse.visibility = View.GONE
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


        // Bottom Navigation
        binding.bottomNavigation.setSelectedItemId(R.id.Attendance_btmNavigation)
        binding.bottomNavigation.setOnItemSelectedListener {

            when(it.itemId){
                R.id.Schedule_btmNavigation ->{
                    vibrator.vibrate(50)
                    startActivity(Intent(this, Daily_Schedule::class.java))
                    finish()
                }

                R.id.Tasks_btmNavigation ->{
                    vibrator.vibrate(50)
                    startActivity(Intent(this, TaskActivity::class.java))
                    finish()
                }
            }

            return@setOnItemSelectedListener true
        }


//        howToUse.setOnClickListener {
//            vibrator.vibrate(50)
//            val intent=Intent(this, instructionsPage::class.java)
//            startActivity(intent)
//        }
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

            arrAttendance.add(Attendance(1, "100%", "Subject 1", "10", "10", currentTime, "75%"))
            arrAttendance.add(Attendance(2, "60%", "Subject 2", "10", "6", currentTime, "75%"))
            arrAttendance.add(Attendance(3, "40%", "Subject 3", "10", "4", currentTime, "75%"))

            GlobalScope.launch {
                database.attendanceDao().insertAttendance(Attendance(1, "100%", "Subject 1", "10", "10", currentTime, "75%"))
                database.attendanceDao().insertAttendance(Attendance(2, "60%", "Subject 2", "10", "6", currentTime,"75%" ))
                database.attendanceDao().insertAttendance(Attendance(3, "40%", "Subject 3", "10", "4", currentTime, "75%"))


            }

            adapter.notifyItemChanged(arrAttendance.size-1)
            sharedPreferenceManager.updateUserVersion(1)


        }


    }

    private fun refresh(){
        adapter.notifyItemChanged(arrAttendance.size-1)
    }
}