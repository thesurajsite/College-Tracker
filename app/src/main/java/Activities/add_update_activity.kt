package Activities

import AttendanceRoomDatabase.DatabaseHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.collegetracker.R
import com.collegetracker.databinding.ActivityAddUpdateBinding
import com.collegetracker.databinding.ActivityMainBinding
import com.collegetracker.databinding.ActivityMainBinding.bind
import com.collegetracker.databinding.ActivityMainBinding.inflate

class add_update_activity : AppCompatActivity() {

    private lateinit var database: DatabaseHelper
    lateinit var binding:ActivityAddUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialization of Database
        database = DatabaseHelper.getDB(applicationContext) ?: throw IllegalStateException("Unable to create database instance")

        //Receiving subject data through intent
        val subjectID=intent.getIntExtra("subjectId",1)
        val subjectName=intent.getStringExtra("subject")
        val conducted=intent.getStringExtra("conducted")
        val attended=intent.getStringExtra("attended")


        binding.subjectName.setText(subjectName)


    }
}