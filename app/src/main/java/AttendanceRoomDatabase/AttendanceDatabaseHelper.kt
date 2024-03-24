package AttendanceRoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities =[Attendance::class], version = 7)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDAO

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
                    .addMigrations(migration_5_6, migration_6_7)
                    .build()
            }
            return instance
        }
    }
}