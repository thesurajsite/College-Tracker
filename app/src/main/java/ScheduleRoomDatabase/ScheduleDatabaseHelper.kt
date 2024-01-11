package ScheduleRoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ScheduleDataclass::class], version = 1 )
abstract class ScheduleDatabaseHelper: RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDAO

}