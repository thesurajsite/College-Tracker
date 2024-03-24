package AttendanceRoomDatabase

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey (autoGenerate = true)
    var id: Int,
    var percentage: String,
    var subjectName: String,
    var classesConducted: String,
    var classesAttended: String,
    var lastUpdated: String,
    var requirement: String) {


    // Secondary constructor 2
    @Ignore
    constructor(percentage: String, subjectName: String, classesConducted: String, classesAttended: String, lastUpdated: String, requirement: String)
            : this(0,percentage = percentage, subjectName = subjectName, classesConducted = classesConducted, classesAttended = classesAttended, lastUpdated=lastUpdated, requirement=requirement)

}