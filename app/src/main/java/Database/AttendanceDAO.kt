package Database
import Models.Attendance
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AttendanceDAO {


    @Insert
    suspend fun insertAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance ORDER BY id ASC")
    fun getAllAttendance(): List<Attendance>

    @Query("UPDATE attendance SET percentage=:percentage, subjectName=:subjectName, classesConducted=:classesConducted, classesAttended=:classesAttended, lastUpdated=:lastUpdated, requirement=:requirement WHERE id=:id")
    suspend fun update(id: Int, percentage: String, subjectName: String, classesConducted: String, classesAttended: String, lastUpdated: String, requirement: String)


    @Query("DELETE FROM attendance WHERE id=:id")
    suspend fun deleteAttendance(id: Int)

    @Query("SELECT DISTINCT subjectName from attendance WHERE subjectName IS NOT NULL")
    suspend fun getAllSubjectNames(): List<String>



}