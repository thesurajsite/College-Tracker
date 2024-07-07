package Activities

import Models.TaskDataClass
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.collegetracker.R
import com.collegetracker.databinding.ActivityAddUpdateBinding
import com.collegetracker.databinding.ActivityAddUpdateTasksBinding
import com.collegetracker.databinding.ActivityTaskBinding
import com.google.android.gms.tasks.Task

class AddUpdateTasks : AppCompatActivity() {

    private lateinit var binding: ActivityAddUpdateTasksBinding
    private lateinit var task: TaskDataClass
    private lateinit var old_task: TaskDataClass
    lateinit var vibrator: Vibrator
    var isUpdate=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAddUpdateTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        try{

            old_task=intent.getSerializableExtra("current_task") as TaskDataClass
            binding.taskName.setText(old_task.taskName)
            binding.taskDetails.setText(old_task.taskDetails)
            binding.priority.setText(old_task.priority)
            isUpdate=true

        }catch (e: Exception){
            e.printStackTrace()
        }

        binding.saveButton.setOnClickListener {
            vibrator.vibrate(50)

            val name=binding.taskName.text.toString()
            val details= binding.taskDetails.text.toString()
            val priority= binding.priority.text.toString()

            if(name.isNotEmpty()){

                if(isUpdate)
                    task=TaskDataClass(old_task.id, name, priority, details, old_task.isComplete)
                else
                    task=TaskDataClass(null, name, priority, details, false)


                val intent= Intent()
                intent.putExtra("task", task)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            else{
                Toast.makeText(this, "Please Enter Task", Toast.LENGTH_SHORT).show()
            }
        }

    }
}