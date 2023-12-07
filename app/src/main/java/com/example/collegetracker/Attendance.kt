package com.example.collegetracker
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "attendanceTable")
class Attendance {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "subjectName")
    var subjectName: String

    @ColumnInfo(name = "classesConducted")
    var classesConducted: String

    @ColumnInfo(name = "classesAttended")
    var classesAttended: String

    @ColumnInfo(name = "percentage")
    var percentage: String

    internal constructor(id: Int, subjectName: String, classesConducted: String, classesAttended: String, percentage: String) {
        this.id = id
        this.subjectName = subjectName
        this.classesConducted = classesConducted
        this.classesAttended = classesAttended
        this.percentage=percentage
    }

    @Ignore
    internal constructor(subjectName: String, classesConducted: String, classesAttended: String, percentage: String) {
        this.subjectName = subjectName
        this.classesConducted = classesConducted
        this.classesAttended = classesAttended
        this.percentage=percentage
    }
}