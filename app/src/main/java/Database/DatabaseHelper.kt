package Database

import Models.Attendance
import Models.TaskDataClass
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities =[Attendance::class, TaskDataClass::class], version = 1)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDAO
    abstract fun taskDao(): TaskDAO

    companion object {


        const val DB_NAME = "CollegeTracker_DB"
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