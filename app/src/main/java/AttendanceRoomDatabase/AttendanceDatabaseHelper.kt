package AttendanceRoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities =[Attendance::class], version = 6)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDAO

    companion object {
        val migration_5_6=object :Migration(5,6){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE attendance ADD COLUMN lastUpdated TEXT NOT NULL DEFAULT('Yesterday')")
            }
        }
        const val DB_NAME = "AttendanceDB"
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
                    .addMigrations(migration_5_6)
                    .build()
            }
            return instance
        }
    }
}