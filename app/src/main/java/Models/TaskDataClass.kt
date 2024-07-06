package Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "task_table")
data class TaskDataClass(

    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "taskName") val taskName: String?,
    @ColumnInfo(name = "priority") val priority: String?,
    @ColumnInfo(name = "taskDetails") val taskDetails: String?,
    @ColumnInfo(name = "isComplete") val isComplete: Boolean= false


): Serializable
