package Models

import android.content.Context
import android.health.connect.datatypes.units.Percentage
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class ProductivityViewModel : ViewModel() {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val USER_ID= auth.currentUser?.uid.toString()
    private val db: FirebaseFirestore by lazy { Firebase.firestore}

    private val _dayCardList = MutableLiveData<List<DayCard>>()
    val dayCardList : LiveData<List<DayCard>> get() = _dayCardList

    private val _taskList = MutableLiveData<List<DayTask>>()
    val taskList : MutableLiveData<List<DayTask>> get() = _taskList

    fun CreateDayCard(date: String, context: Context) {
        if (USER_ID.isEmpty() || USER_ID == "null") {
            Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show()
            return
        }

        val dayCard = DayCard(date, "0")
        db.collection("PRODUCTIVITY").document(USER_ID).collection("DAY_CARDS").document(date).set(dayCard)
            .addOnSuccessListener {
                Toast.makeText(context, "DayCard Created", Toast.LENGTH_SHORT).show()
                FetchDayCards(context)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to create: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun DeleteDayCard(date: String, context: Context) {
        db.collection("PRODUCTIVITY").document(USER_ID).collection("DAY_CARDS").document(date).delete()
            .addOnSuccessListener {
                Toast.makeText(context, "DayCard Deleted", Toast.LENGTH_SHORT).show()
                FetchDayCards(context)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to Delete: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun UpdateDayCard(date: String, percentage: String, context: Context) {
        val dayCard = DayCard(date, percentage)
        db.collection("PRODUCTIVITY").document(USER_ID).collection("DAY_CARDS").document(date).set(dayCard)
            .addOnSuccessListener {
                FetchDayCards(context)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun FetchDayCards(context: Context) {
        db.collection("PRODUCTIVITY").document(USER_ID).collection("DAY_CARDS").get()
            .addOnSuccessListener { snapshot ->
                val dayCards = mutableListOf<DayCard>()
                for (document in snapshot.documents) {
                    val dayCard = document.toObject(DayCard::class.java)
                    if (dayCard != null) {
                        dayCards.add(dayCard)
                    }
                }

                val inputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val sortedList = dayCards
                    .filter { !it.date.isNullOrEmpty() }
                    .sortedByDescending { inputFormat.parse(it.date) }

                _dayCardList.value = sortedList
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FetchDayCards", "Error fetching", e)
            }
    }


    fun AddTaskToDayCard(date: String, newTask: DayTask, context: Context) {
        val taskCollectionRef = db.collection("PRODUCTIVITY").document(USER_ID).collection("DAY_CARDS").document(date).collection("TASKS")
        val taskId = taskCollectionRef.document().id
        val taskWithId = newTask.copy(taskId = taskId)

        taskCollectionRef.document(taskId).set(taskWithId)
            .addOnSuccessListener {
                FetchTaskList(date) // update the task list
                // update percentage in DayCard
                fetchCompletePercentForDayCard(date) { percentage->
                    UpdateDayCard(date, percentage.toString(), context)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to add task: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddTaskToDayCard", "Error", e)
            }
    }

    fun UpdateTask(date: String, updatedTask: DayTask, context: Context) {
        val taskCollectionRef = db.collection("PRODUCTIVITY").document(USER_ID).collection("DAY_CARDS").document(date).collection("TASKS")
        val taskId = updatedTask.taskId

        taskCollectionRef.document(taskId!!).set(updatedTask)
            .addOnSuccessListener {
                // update percentage in DayCard
                fetchCompletePercentForDayCard(date) { percentage->
                    UpdateDayCard(date, percentage.toString(), context)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to add task: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddTaskToDayCard", "Error", e)
            }
    }

    fun DeleteTask(date: String, taskId: String, context: Context) {
        db.collection("PRODUCTIVITY").document(USER_ID).collection("DAY_CARDS").document(date).collection("TASKS").document(taskId).delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show()
                FetchTaskList(date)
                // update percentage in DayCard
                fetchCompletePercentForDayCard(date) { percentage->
                    UpdateDayCard(date, percentage.toString(), context)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to delete task: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("DeleteTaskFromDayCard", "Error", e)
            }
    }


    fun FetchTaskList(date: String) {
        db.collection("PRODUCTIVITY").document(USER_ID).collection("DAY_CARDS").document(date).collection("TASKS").get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(DayTask::class.java)
                }
                _taskList.value = tasks
            }
            .addOnFailureListener { e ->
                Log.e("FetchTaskList", "Error fetching tasks", e)
            }
    }

    fun fetchCompletePercentForDayCard(date: String, onResult: (Int) -> Unit) {
        db.collection("PRODUCTIVITY").document(USER_ID).collection("DAY_CARDS").document(date).collection("TASKS").get()
            .addOnSuccessListener { snapshot ->
                val tasks = snapshot.toObjects(DayTask::class.java)
                val total = tasks.size
                val done = tasks.count { it.complete == true }
                val percent = if (total > 0) (done * 100) / total else 0

                onResult(percent)
            }
            .addOnFailureListener { e ->
                Log.e("FetchPercent", "Error: ${e.message}", e)
                onResult(0)
            }
    }

}

data class DayCard(
    val date: String?="",
    val percentage: String?=""
)

data class DayTask(
    val taskId: String ?= "",
    val taskName: String ?= "",
    val complete: Boolean ?= false,
    val time: String ?= "",
    val date: String ?=""
)
