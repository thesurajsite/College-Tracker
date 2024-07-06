package Activities

import Adapters.RecyclerTaskAdapter
import AttendanceRoomDatabase.DatabaseHelper
import Models.TaskDataClass
import Models.TaskViewModel
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.collegetracker.R
import com.collegetracker.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity(), RecyclerTaskAdapter.TaskClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityTaskBinding
    private lateinit var database: DatabaseHelper
    lateinit var viewModel: TaskViewModel
    lateinit var adapter: RecyclerTaskAdapter
    lateinit var selectedTask: TaskDataClass

    private val updateTask= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->

        if(result.resultCode == Activity.RESULT_OK){
            val task= result.data?.getSerializableExtra("task") as? TaskDataClass
            if(task!=null){
                viewModel.updateTask(task)
            }

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Initializing the UI
        initUI()

        viewModel= ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as Application)).get(TaskViewModel::class.java)


        viewModel.allTasks.observe(this) { list->

            list?.let{
                adapter.updateList(list)
            }
        }

        database= DatabaseHelper.getDB(this)!!

        // Bottom Navigation
        binding.bottomNavigation.setSelectedItemId(R.id.Tasks_btmNavigation)
        binding.bottomNavigation.setOnItemSelectedListener {

            when(it.itemId){

                R.id.Attendance_btmNavigation ->{
                    vibrator.vibrate(50)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                R.id.Schedule_btmNavigation ->{
                    vibrator.vibrate(50)
                    startActivity(Intent(this, Daily_Schedule::class.java))
                    finish()
                }

            }

            return@setOnItemSelectedListener true
        }



    }

    private fun initUI() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter= RecyclerTaskAdapter(this, this)
        binding.recyclerView.adapter= adapter

        val getContent= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->

            if(result.resultCode == Activity.RESULT_OK){

                val task=result.data?.getSerializableExtra("task") as? TaskDataClass
                if(task!= null){
                    viewModel.insertTask(task)
                }

            }

        }

        binding.floatingActionButton.setOnClickListener {
            val intent= Intent(this, AddUpdateTasks::class.java)
            getContent.launch(intent)
        }


    }

    override fun onItemClicked(task: TaskDataClass) {
        val intent= Intent(this, AddUpdateTasks::class.java)
        intent.putExtra("current_task", task)
        updateTask.launch(intent)
    }

    override fun onLongItemClicked(task: TaskDataClass, taskRow: CardView) {
        selectedTask = task
        popUpDisplay(taskRow)
    }

    private fun popUpDisplay(taskRow: CardView) {
        val popup= PopupMenu(this, taskRow)
        popup.setOnMenuItemClickListener(this)
        popup.show()

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_task){

            viewModel.deleteTask(selectedTask)
            return true

        }
        return false
    }
}