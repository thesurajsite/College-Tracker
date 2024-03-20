package Activities

import AttendanceRoomDatabase.Attendance
import AttendanceRoomDatabase.DatabaseHelper
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.collegetracker.R
import com.collegetracker.databinding.ActivityAddUpdateBinding
import com.collegetracker.databinding.ActivityMainBinding
import com.collegetracker.databinding.ActivityMainBinding.bind
import com.collegetracker.databinding.ActivityMainBinding.inflate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class add_update_activity : AppCompatActivity() {

    private lateinit var database: DatabaseHelper
    lateinit var binding:ActivityAddUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        //Initialization of Database
        database = DatabaseHelper.getDB(applicationContext) ?: throw IllegalStateException("Unable to create database instance")

        //Receiving subject data through intent
        val subjectID=intent.getIntExtra("subjectId",1)
        var subjectName=intent.getStringExtra("subject")
        val conducted=intent.getStringExtra("conducted")
        val attended=intent.getStringExtra("attended")
        var lastUpdated=intent.getStringExtra("lastUpdated")

        var percentage="0"
        if(conducted!=null && attended!=null){
            percentage=(((attended.toDouble()/conducted.toDouble())*100).toInt()).toString()+"%"
        }

        //binding.horizontalProgressBar.progress=50


        binding.subjectName.setText(subjectName)
        binding.conductedEt.setText(conducted)
        binding.attendedEt.setText(attended)
        binding.percentage.setText(percentage)
        binding.lastUpdated.setText("Last Updated: $lastUpdated")

        if(conducted!=null && attended!=null){

            var tempConducted= conducted.toInt()
            var tempAttended= attended.toInt()

            if(tempAttended<=tempConducted){
                binding.minusConducted.setOnClickListener {
                    vibrator?.vibrate(50)
                    tempConducted--
                    binding.conductedEt.setText(tempConducted.toString())
                    percentage=(((tempAttended.toDouble()/tempConducted.toDouble())*100).toInt()).toString()+"%"
                    binding.percentage.setText(percentage)
                }

                binding.plusConducted.setOnClickListener {
                    vibrator?.vibrate(50)
                    tempConducted++
                    binding.conductedEt.setText(tempConducted.toString())
                    percentage=(((tempAttended.toDouble()/tempConducted.toDouble())*100).toInt()).toString()+"%"
                    binding.percentage.setText(percentage)
                }

                binding.minusAttended.setOnClickListener {
                    vibrator?.vibrate(50)
                    tempAttended--
                    binding.attendedEt.setText(tempAttended.toString())
                    percentage=(((tempAttended.toDouble()/tempConducted.toDouble())*100).toInt()).toString()+"%"
                    binding.percentage.setText(percentage)
                }

                binding.plusAttended.setOnClickListener {
                    vibrator?.vibrate(50)
                    tempAttended++
                    binding.attendedEt.setText(tempAttended.toString())
                    percentage=(((tempAttended.toDouble()/tempConducted.toDouble())*100).toInt()).toString()+"%"
                    binding.percentage.setText(percentage)
                }
            }

            //UPDATE BUTTON
            binding.updateButton.setOnClickListener {
                vibrator?.vibrate(50)

                var conductedName=binding.conductedEt.text.toString()
                var attendedName=binding.attendedEt.text.toString()
                var percentageName=binding.percentage.text.toString()
                var currentTime=currentTime().toString()


                var dataToUpdate: Attendance = Attendance(
                    id=subjectID,
                    percentage = percentageName,
                    subjectName = subjectName!!,
                    classesConducted = conductedName,
                    classesAttended = attendedName,
                    lastUpdated = currentTime
                )


                GlobalScope.launch {
                    database.attendanceDao().updateAttendance(dataToUpdate)
                }

                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }





        }

        //ON BACK-PRESSES
        val onBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                vibrator?.vibrate(50)
                startActivity(Intent(this@add_update_activity, MainActivity::class.java))
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)


    }

    private fun currentTime(): String? {
        val currentDateTime = LocalDateTime.now()

        // Format the date and time with the desired pattern (dd-MM HH:mm)
        val formatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm")
        val DateTime = currentDateTime.format(formatter).toString()

        return DateTime
    }

}