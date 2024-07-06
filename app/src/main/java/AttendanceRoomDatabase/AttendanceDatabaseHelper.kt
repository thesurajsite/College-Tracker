package AttendanceRoomDatabase

import Database.TaskDAO
import Models.TaskDataClass
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities =[Attendance::class, TaskDataClass::class], version = 8)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDAO
    abstract fun taskDao(): TaskDAO

    companion object {

        val migration_5_6=object :Migration(5,6){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE attendance ADD COLUMN lastUpdated TEXT NOT NULL DEFAULT('Yesterday')")
            }
        }

        val migration_6_7=object :Migration(6,7){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE attendance ADD COLUMN requirement TEXT NOT NULL DEFAULT('75%')")
            }
        }

        val migration_7_8 = object : Migration(7,8) {  // New migration for TaskDataClass
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `task_table` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `taskName` TEXT, 
                        `priority` TEXT, 
                        `taskDetails` TEXT
                        `isComplete` INTEGER NOT NULL DEFAULT 0
                    )
                """)
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
                    .addMigrations(migration_5_6, migration_6_7, migration_7_8)
                    .build()
            }
            return instance
        }
    }
}