package RoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities =[Attendance::class], version = 5)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDAO

//    companion object {
//        const val DB_NAME = "Attendancedb"
//        var instance: DatabaseHelper? = null
//        @Synchronized
//        fun getDB(context: Context?): DatabaseHelper? {
//            if (instance == null) {
//                instance = Room.databaseBuilder(
//                    context!!.applicationContext,
//                    DatabaseHelper::class.java, DB_NAME
//                )
//                    .fallbackToDestructiveMigration()
//                    .allowMainThreadQueries()
//                    .build()
//            }
//            return instance
//        }
//    }
}