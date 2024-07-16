package Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "attendance")
data class Attendance(

    @PrimaryKey (autoGenerate = true) var id: Int,
    @ColumnInfo(name = "percentage") var percentage: String,
    @ColumnInfo(name = "subjectName") var subjectName: String,
    @ColumnInfo(name = "classesConducted") var classesConducted: String,
    @ColumnInfo(name = "classesAttended") var classesAttended: String,
    @ColumnInfo(name = "lastUpdated") var lastUpdated: String,
    @ColumnInfo(name = "requirement") var requirement: String

): Serializable
