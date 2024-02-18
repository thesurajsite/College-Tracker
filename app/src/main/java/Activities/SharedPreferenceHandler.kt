package Activities

import android.content.SharedPreferences

interface SharedPreferenceHandler {

    fun getScheduleID(): Int

    fun updateScheduleID(updatedID: Int)

    fun getAttendanceID(): Int

    fun updateAttendanceID(updatedID: Int)
    fun getSharedPreferences(s: String, modePrivate: Int): SharedPreferences

}