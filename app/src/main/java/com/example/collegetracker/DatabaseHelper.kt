package com.example.collegetracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities =[Attendance::class], exportSchema = true, version = 2)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun attendanceDao(): Attendance?

    companion object {
        const val DB_NAME = "Attendancedb"
        var instance: DatabaseHelper? = null
        @Synchronized
        fun getDB(context: Context?): DatabaseHelper? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context!!.applicationContext,
                    DatabaseHelper::class.java, DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}