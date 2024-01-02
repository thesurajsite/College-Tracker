package Activities

import ScheduleFragments.scheduleViewPagerAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.collegetracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.time.DayOfWeek

import java.time.LocalDate

class DailySchedule : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_schedule)

        val scheduleButton = findViewById<ImageView>(R.id.scheduleButton)
        scheduleButton.visibility = View.GONE


        // VIEWPAGER DECLARATIONS
        val scheduleTabLayout = findViewById<TabLayout>(R.id.scheduleTabLayout)
        val scheduleViewPager = findViewById<ViewPager2>(R.id.scheduleViewPager)
        val scheduleViewPagerAdapter = scheduleViewPagerAdapter(supportFragmentManager, lifecycle)
        val helpButton = findViewById<ImageView>(R.id.helpButton)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        scheduleViewPager.adapter = scheduleViewPagerAdapter

        //SETTING THE DEFAULT FRAGMENT ACCORDING TO THE DAY

        // Get the current day of the week (e.g., MONDAY, TUESDAY)
        val currentDayOfWeek = LocalDate.now().dayOfWeek
        Toast.makeText(this, currentDayOfWeek.toString(), Toast.LENGTH_SHORT).show()
        var indexDay: Int = getTabIndexForDay(currentDayOfWeek)
        scheduleViewPager.currentItem = indexDay

        // Manually set the selected tab
        scheduleTabLayout.getTabAt(indexDay)?.select()



        scheduleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab selected
                if (tab != null) {
                    scheduleViewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselected
            }
        })

        scheduleViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                scheduleTabLayout.selectTab(scheduleTabLayout.getTabAt(position))
            }
        })


        helpButton.setOnClickListener {
            vibrator.vibrate(50)
            val intent = Intent(this, instructionsPage::class.java)
            startActivity(intent)
        }

    }

    private fun getTabIndexForDay(dayOfWeek: DayOfWeek): Int {
        return when (dayOfWeek) {

            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
        }
    }
}