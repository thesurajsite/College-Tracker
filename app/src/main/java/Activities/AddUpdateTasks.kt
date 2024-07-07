package Activities

import Adapters.customSpinnerAdapter
import Models.TaskDataClass
import Models.TaskViewModel
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.collegetracker.R
import com.collegetracker.databinding.ActivityAddUpdateTasksBinding


class AddUpdateTasks : AppCompatActivity() {

    private lateinit var binding: ActivityAddUpdateTasksBinding
    private lateinit var task: TaskDataClass
    private lateinit var old_task: TaskDataClass
    lateinit var viewModel: TaskViewModel
    lateinit var vibrator: Vibrator
    var isUpdate=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAddUpdateTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as Application)).get(TaskViewModel::class.java)


        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        spinner()

        try{

            old_task=intent.getSerializableExtra("current_task") as TaskDataClass
            binding.taskName.setText(old_task.taskName)
            binding.taskDetails.setText(old_task.taskDetails)

            if(old_task.priority=="Low")
                binding.priority.setSelection(1)
            else if(old_task.priority == "Medium")
                binding.priority.setSelection(2)
            else if (old_task.priority == "High")
                binding.priority.setSelection(3)
            else binding.priority.setSelection(1)

            isUpdate=true

        }catch (e: Exception){
            e.printStackTrace()
        }

        binding.saveButton.setOnClickListener {
            vibrator.vibrate(50)

            val name=binding.taskName.text.toString()
            val details= binding.taskDetails.text.toString()
            val priority= binding.priority.selectedItem.toString()

            if(name.isNotEmpty()){

                if(isUpdate)
                    task=TaskDataClass(old_task.id, name, priority, details, old_task.isComplete)
                else
                    task=TaskDataClass(null, name, priority, details, false)

                if(priority!="Select Priority"){
                    val intent= Intent()
                    intent.putExtra("task", task)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }     
                else Toast.makeText(this, "Select Priority", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Please Enter Task", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteButton.setOnClickListener {
            vibrator.vibrate(50)

            val builder = AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setIcon(R.drawable.baseline_delete_24)
                .setMessage("Do you want to Delete this Task ?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->
                    try {

                        vibrator.vibrate(50)
                        Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show()
                        viewModel.deleteTask(old_task)
                        startActivity(Intent(this, TaskActivity::class.java))
                        finish()


                    } catch (e: Exception) {
                        Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show()
                        Log.w("crash-delete", e)
                    }

                }.setNegativeButton("No")
                { dialogInterface, i ->
                    vibrator.vibrate(50)
                    Toast.makeText(this, "Deletion Cancelled", Toast.LENGTH_SHORT).show()
                }
            builder.show()

        }

    }

    private fun spinner() {
        // Hostel Spinner
        val list_of_priority= arrayListOf<String>("Select Priority","Low", "Medium", "High")
        val prioritySpinnerAdapter= customSpinnerAdapter(this, list_of_priority)
        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priority.adapter=prioritySpinnerAdapter
    }
}