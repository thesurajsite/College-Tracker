package Activities

import ScheduleFragments.scheduleViewPagerAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.collegetracker.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Calendar

class Daily_Schedule : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_schedule2)

        //THE ONLY CODE FOR VIEWPAGER
        val scheduleViewPager: ViewPager2 = findViewById(R.id.scheduleViewPager)
        val adapter = scheduleViewPagerAdapter(supportFragmentManager, lifecycle)
        scheduleViewPager.adapter=adapter

        // Set the desired offscreen page limit
        val newOffscreenLimit = 7
        scheduleViewPager.offscreenPageLimit = newOffscreenLimit


        // SET THE DEFAULT FRAGMENT TO OPEN BY DEFAULT ACCORDING TO CURRENT DAY
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val defaultDayIndex: Int

        if (currentDay == Calendar.SUNDAY) {
            defaultDayIndex = 6 // Sunday
        } else if (currentDay == Calendar.MONDAY) {
            defaultDayIndex = 0 // Monday
        } else if (currentDay == Calendar.TUESDAY) {
            defaultDayIndex = 1 // Tuesday
        } else if (currentDay == Calendar.WEDNESDAY) {
            defaultDayIndex = 2 // Wednesday
        } else if (currentDay == Calendar.THURSDAY) {
            defaultDayIndex = 3 // Thursday
        } else if (currentDay == Calendar.FRIDAY) {
            defaultDayIndex = 4 // Friday
        } else { // Assuming currentDay == Calendar.SATURDAY
            defaultDayIndex = 5 // Saturday
        }
        scheduleViewPager.currentItem = defaultDayIndex


        //TAB LAYOUT AND SYNCING WITH VIEWPAGER2
        val scheduleTabLayout = findViewById<TabLayout>(R.id.scheduleTabLayout)
        val tabNames = arrayOf("M", "T", "W", "T", "F", "S", "S")
        TabLayoutMediator(scheduleTabLayout, scheduleViewPager) { tab, position ->
            tab.text=tabNames[position]
        }.attach()

        //OTHER ELEMENTS
        val helpButton = findViewById<ImageView>(R.id.helpButton)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        // COMMANDS FOR HELP BUTTON AND SCHEDULE BUTTON
        helpButton.setOnClickListener {
            vibrator.vibrate(50)
            val intent = Intent(this, instructionsPage::class.java)
            startActivity(intent)
        }

        val scheduleButton = findViewById<ImageView>(R.id.scheduleButton)
        scheduleButton.visibility = View.GONE

        // Set a custom PageTransformer for smooth sliding animation
        scheduleViewPager.setPageTransformer { page, position ->
            val absPosition = Math.abs(position)
            page.alpha = 1f - absPosition
            page.scaleY = 0.85f + 0.15f * (1f - absPosition)
        }

    }
}
