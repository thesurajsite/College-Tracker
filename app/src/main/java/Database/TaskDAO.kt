package Database

import Models.TaskDataClass
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskDataClass)

    @Delete
    suspend fun delete(task: TaskDataClass)

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    fun getAllTask(): LiveData<List<TaskDataClass>>

    @Query("UPDATE task_table SET taskName= :taskName, priority= :priority, taskDetails= :taskDetails, isComplete= :isComplete WHERE id= :id ")
    suspend fun update(id: Int?, taskName: String?, priority: String?, taskDetails: String?, isComplete: Boolean?)


}