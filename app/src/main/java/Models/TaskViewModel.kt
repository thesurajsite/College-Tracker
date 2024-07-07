package Models

import Database.DatabaseHelper
import Database.Repository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {

    private val repository: Repository
    val allTasks : LiveData<List<TaskDataClass>>

    init {
        val dao= DatabaseHelper.getDB(application)?.taskDao()
        repository= dao?.let { Repository(it) }!!
        allTasks=repository.allTasks
    }

    fun deleteTask(task: TaskDataClass)= viewModelScope.launch ( Dispatchers.IO ){
        repository.delete(task)
    }

    fun insertTask(task: TaskDataClass)= viewModelScope.launch ( Dispatchers.IO ){
        repository.insert(task)
    }

    fun updateTask(task: TaskDataClass)= viewModelScope.launch ( Dispatchers.IO ){
        repository.update(task)
    }



}