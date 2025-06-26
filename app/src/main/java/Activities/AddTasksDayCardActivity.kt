package Activities

import Adapters.DayCardRecyclerAdapter
import Adapters.DayTaskRecyclerAdapter
import Models.DayTask
import Models.ProductivityViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.collegetracker.R
import com.collegetracker.databinding.ActivityAddtasksDaycardBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AddTasksDayCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddtasksDaycardBinding
    private lateinit var productivityViewModel: ProductivityViewModel
    private var taskList = mutableListOf<DayTask>()
    private lateinit var adapter: DayTaskRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddtasksDaycardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productivityViewModel = ProductivityViewModel()
        
        val date = intent.getStringExtra("date").toString()
        binding.dateTextView.text = date


        taskList = mutableListOf<DayTask>()
        adapter = DayTaskRecyclerAdapter(this, taskList, productivityViewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.notifyDataSetChanged()


        // Fetch the Task List
        productivityViewModel.FetchTaskList(date)
        productivityViewModel.taskList.observe(this) { list ->
            taskList.clear()
            taskList.addAll(list)
            adapter.notifyDataSetChanged()
            //Toast.makeText(this, "Tasks fetched: ${taskList.size}", Toast.LENGTH_SHORT).show()
        }


        binding.addTaskCardView.setOnClickListener {
            val taskName = binding.taskEditText.text.toString().trim()
            if (taskName.isNotEmpty()) {
                val time = getCurrentTimestamp()
                val newTask = DayTask("", taskName, false, time, date)
                productivityViewModel.AddTaskToDayCard(date, newTask, this)
                binding.taskEditText.text.clear()
            }
        }


    }

    fun getCurrentTimestamp(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(System.currentTimeMillis())
    }

}