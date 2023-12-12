package RoomDatabase

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var percentage: String,
    var subjectName: String,
    var classesConducted: String,
    var classesAttended: String) {


    // Secondary constructor 2
    @Ignore
    constructor(percentage: String, subjectName: String, classesConducted: String, classesAttended: String)
            : this(0,percentage = percentage, subjectName = subjectName, classesConducted = classesConducted, classesAttended = classesAttended)

}


























//class Attendance {
//    @PrimaryKey(autoGenerate = true)
//    var id = 0
//
//    @ColumnInfo(name = "percentage")
//    var percentage: String
//
//    @ColumnInfo(name = "subjectName")
//    var subjectName: String
//
//    @ColumnInfo(name = "classesConducted")
//    var classesConducted: String
//
//    @ColumnInfo(name = "classesAttended")
//    var classesAttended: String
//
//    internal constructor(id: Int, percentage: String, subjectName: String, classesConducted: String, classesAttended: String) {
//        this.id = id
//        this.percentage=percentage
//        this.subjectName = subjectName
//        this.classesConducted = classesConducted
//        this.classesAttended = classesAttended
//    }
//
//    @Ignore
//    internal constructor(percentage: String,subjectName: String, classesConducted: String, classesAttended: String) {
//        this.percentage=percentage
//        this.subjectName = subjectName
//        this.classesConducted = classesConducted
//        this.classesAttended = classesAttended
//    }
//}