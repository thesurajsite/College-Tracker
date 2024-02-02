package AttendanceRoomDatabase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AttendanceDAO {


    @Insert
    fun insertAttendance(attendance: Attendance)

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("UPDATE attendance SET percentage=:percentage, subjectName=:subjectName, classesConducted=:classesConducted, classesAttended=:classesAttended, lastUpdated=:lastUpdated WHERE id=:id")
    suspend fun update(id: Int, percentage: String, subjectName: String, classesConducted: String, classesAttended: String, lastUpdated: String)

    @Query("DELETE FROM attendance WHERE id=:id")
    suspend fun deleteAttendance(id: Int)


    @Query("SELECT * FROM attendance")
    fun getAllAttendance(): List<Attendance>

    @Query("SELECT DISTINCT subjectName from attendance WHERE subjectName IS NOT NULL")
    suspend fun getAllSubjectNames(): List<String>



}