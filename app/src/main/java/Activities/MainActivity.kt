package Activities

import Models.Attendance
import Database.DatabaseHelper
import Models.AttendenceModel
import Adapters.RecyclerAttendanceAdapter
import Models.AttendanceViewModel
import Notifications.PermissionManager
import Notifications.createNotificationChannel
import Notifications.scheduleDailyNotification
import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.collegetracker.R
import com.collegetracker.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private lateinit var permissionManager: PermissionManager
    lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore by lazy { Firebase.firestore}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionManager = PermissionManager(this)

        appUpdateManager=AppUpdateManagerFactory.create(applicationContext)
        if(updateType==AppUpdateType.FLEXIBLE){
            appUpdateManager.registerListener(installStateUpdateListener)
        }

        checkForAppUpdate()

        //Initialization of Database
        database = DatabaseHelper.getDB(applicationContext) ?: throw IllegalStateException("Unable to create database instance")
        sharedPreferenceManager=sharedPreferenceManager(this)

        // ViewModel
        viewModel=ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as Application))
            .get(AttendanceViewModel::class.java)

        // RecyclerView and ArrayList
        arrAttendance=ArrayList()
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = RecyclerAttendanceAdapter(this, arrAttendance, viewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager=LinearLayoutManager(this)
        val floatingActionButton=findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val nothingToShowImage: ImageView = findViewById(R.id.nothingToShow)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        auth=FirebaseAuth.getInstance()
        val googleCardView = findViewById<CardView>(R.id.GoogleCardView)
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val USER_ID= auth.currentUser?.uid.toString()

        //Google Authentication
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)


        // SHARED PREFERENCES FOR WHICH ACTIVITY TO OPEN ON STARTUP
        openStartupActivity()
        //SHARED PREFERENCE FOR NEW USER, EXECUTES ONE TIME ONLY
        sharedPreferenceForNewUser()

        // Notifications
        if (permissionManager.hasNotificationPermission()) {
            if (permissionManager.hasExactAlarmPermission()) {
                // Create Notifications Channel
                createNotificationChannel(this)
                // Schedule Daily Notifications
                scheduleDailyNotification(this)
            } else {
                permissionManager.requestExactAlarmPermission(this)
            }
        } else {
            permissionManager.requestNotificationPermission(this)
        }



        adapter.notifyItemChanged(arrAttendance.size-1)
        adapter.notifyDataSetChanged()
        refresh()

        // POPULATE THE RECYCLERVIEW ON ACTIVITY STARTUP
        progressBar.visibility = View.VISIBLE
        if(auth.currentUser==null){
            viewModel.allAttendance.observe(this){ list->
                list?.let {
                    arrAttendance.clear()
                    arrAttendance.addAll(list)
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE

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
        }
        else{
            val reference = db.collection("ATTENDANCE").document(USER_ID).collection("USER_ATTENDANCE")
            reference.orderBy("subjectName", Query.Direction.DESCENDING).get()
                .addOnSuccessListener {
                    //Toast.makeText(activity, "Contacts Fetched", Toast.LENGTH_SHORT).show()

                    val attendanceList = mutableListOf<Attendance>()

                    for (document in it.documents) {
                        val firebaseId = document.getString("firebaseId") ?: ""
                        val classesAttended = document.getString("classesAttended") ?: ""
                        val classesConducted = document.getString("classesConducted") ?: ""
                        val lastUpdated = document.getString("lastUpdated") ?: ""
                        val percentage = document.getString("percentage") ?: ""
                        val requirement = document.getString("requirement") ?: ""
                        val subjectName = document.getString("subjectName") ?: ""

                        attendanceList.add(Attendance(0, firebaseId, percentage, subjectName, classesConducted, classesAttended, lastUpdated, requirement))
                    }
                    arrAttendance.clear()
                    arrAttendance.addAll(attendanceList)
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Some error occured fetching contact", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }

        }

        // Fetch User Profile Image
        val user = auth.currentUser
        if(user!=null){
            val photoUrl = user.photoUrl
            if(photoUrl!=null){
                Glide.with(this@MainActivity)
                    .load(photoUrl)
                    .into(profileImage)
            }

        }

        googleCardView.setOnClickListener {
            vibrator.vibrate(50)

            val user = auth.currentUser
            if(user==null){
                googleSignInClient.signOut()
                startActivityForResult(googleSignInClient.signInIntent, 123)
            }
            else{
                val builder = AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setIcon(R.drawable.baseline_logout_24)
                    .setMessage("Do you want to Logout ?")
                    .setPositiveButton(
                        "Yes"
                    ) { dialogInterface, i ->
                        try {

                            Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                            auth.signOut()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()


                        } catch (e: Exception) {
                            Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                            Log.w("crash-attendance", e)
                        }

                    }.setNegativeButton("No")
                    { dialogInterface, i ->
                        TODO()
                    }
                builder.show()
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

                    // Adding Attendance

                    if(auth.currentUser==null){
                        val attendance = Attendance(0,"", percentageString, subjectName, conductedName, attendedName, currentTime, "75%")
                        viewModel.insertAttendance(attendance)
                        adapter.notifyItemChanged(arrAttendance.size - 1)
                        recyclerView.scrollToPosition(arrAttendance.size - 1)
                        dialog.dismiss()
//                        startActivity(Intent(this, MainActivity::class.java))
//                        finish()
                    }
                    else{
                        val ATTENDANCE_ID = db.collection("ATTENDANCE").document(USER_ID).collection("USER_ATTENDANCE").document().id
                        val attendance = Attendance(0, ATTENDANCE_ID, percentageString, subjectName, conductedName, attendedName, currentTime, "75%")
                        db.collection("ATTENDANCE").document(USER_ID).collection("USER_ATTENDANCE").document(ATTENDANCE_ID).set(attendance)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                                val intent =  Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()

                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Some error occured while Saving", Toast.LENGTH_SHORT).show()
                            }
                    }


                    // VISIBILITY CONTROL FOR nothingToShow IMAGE
                    if (arrAttendance.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        nothingToShowImage.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        nothingToShowImage.visibility = View.GONE
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
                    sharedPreferenceManager.updateNavigationCode(2)
                    startActivity(Intent(this, Daily_Schedule::class.java))
                    finish()
                }

                R.id.Tasks_btmNavigation ->{
                    vibrator.vibrate(50)
                    sharedPreferenceManager.updateNavigationCode(3)
                    startActivity(Intent(this, TaskActivity::class.java))
                    finish()
                }
            }

            return@setOnItemSelectedListener true
        }

    }

    fun openStartupActivity() {

        val activityCode =  sharedPreferenceManager.getNavigationCode()
        if(activityCode==1){
            // MainAcvity Remains Open
        }
        else if(activityCode==2){
            val intent = Intent(this, Daily_Schedule::class.java)
            startActivity(intent)
        }
        else if(activityCode==3){
            val intent = Intent(this, TaskActivity::class.java)
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

        if (requestCode == 123 && resultCode ==  RESULT_OK) {
            Log.d("College Tracker", "onActivityResult")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)

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

            arrAttendance.add(Attendance(1, "","100%", "Subject 1", "10", "10", currentTime, "75%"))
            arrAttendance.add(Attendance(2,"", "60%", "Subject 2", "10", "6", currentTime, "75%"))
            arrAttendance.add(Attendance(3, "","40%", "Subject 3", "10", "4", currentTime, "75%"))

            GlobalScope.launch {
                database.attendanceDao().insertAttendance(Attendance(1, "","100%", "Subject 1", "10", "10", currentTime, "75%"))
                database.attendanceDao().insertAttendance(Attendance(2, "","60%", "Subject 2", "10", "6", currentTime,"75%" ))
                database.attendanceDao().insertAttendance(Attendance(3, "","40%", "Subject 3", "10", "4", currentTime, "75%"))


            }

            adapter.notifyItemChanged(arrAttendance.size-1)
            sharedPreferenceManager.updateUserVersion(1)


        }


    }

    private fun refresh(){
        adapter.notifyItemChanged(arrAttendance.size-1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionManager.REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Notification permission granted
                // Check for exact alarm permission before scheduling notifications
                if (permissionManager.hasExactAlarmPermission()) {
                    // Create Notifications Channel
                    createNotificationChannel(this)
                    // Schedule Daily Notifications
                    scheduleDailyNotification(this)
                } else {
                    // Request exact alarm permission if it's not granted
                    permissionManager.requestExactAlarmPermission(this)
                }
            } else {
                // Notification permission denied
                // Handle the case where the user denied the notification permission
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Signed in Successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Some Error Occured", Toast.LENGTH_LONG).show()
                Log.d("CollegeTracker", it.localizedMessage!!)
            }
    }

}