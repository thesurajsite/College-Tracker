package Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.collegetracker.R

class instructionsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions_page)

        val helpButton=findViewById<ImageView>(R.id.helpButton)
        helpButton.visibility=View.GONE

        val scheduleButton=findViewById<ImageView>(R.id.scheduleButton)
        scheduleButton.visibility=View.GONE

    }
}