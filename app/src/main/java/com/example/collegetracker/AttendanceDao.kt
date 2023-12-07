package com.example.collegetracker
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactsDao {
    @get:Query("select * from attendanceTable")
    val allContacts: List<Any?>?

    @Insert
    fun addAttendance(attendanceTable: Attendance?)

    @Update
    fun updateAttendance(attendanceTable: Attendance?)

    @Delete
    fun deleteAttendance(attendanceTable: Attendance?)
}