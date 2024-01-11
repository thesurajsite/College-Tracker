package ScheduleRoomDatabase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.jetbrains.annotations.Async

@Dao
interface ScheduleDAO {

    @Insert
    fun insertSchedule(schedule: ScheduleDataclass)

    @Update
    suspend fun updateSchedule(schedule: ScheduleDataclass)

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleDataclass)

    @Query("UPDATE schedule SET day=:day, lecture=:lecture, time=:time WHERE id=:id")
    suspend fun update(id: Int, day: String, lecture: String, time: String)

    @Query("DELETE FROM schedule WHERE id=:id")
    suspend fun deleteSchedule(id: Int)


    @Query("SELECT * FROM schedule")
    fun getAllSchedule(): List<ScheduleDataclass>

}