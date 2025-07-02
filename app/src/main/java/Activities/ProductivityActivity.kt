package Activities

import Activities.MainActivity
import Adapters.DayCardRecyclerAdapter
import Adapters.RecyclerAttendanceAdapter
import Functions.GoogleAuthentication
import Models.DayCard
import Models.ProductivityViewModel
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.collegetracker.R
import com.collegetracker.databinding.ActivityProductivityBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProductivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductivityBinding
    private lateinit var sharedPreferenceManager: sharedPreferenceManager
    private lateinit var vibrator : Vibrator
    private lateinit var productivityViewModel: ProductivityViewModel
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val googleAuthentication: GoogleAuthentication by lazy { GoogleAuthentication() }
    val USER_ID= auth.currentUser?.uid.toString()
    private lateinit var adapter: DayCardRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dayCardNameList = mutableListOf<DayCard>()
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        productivityViewModel = ProductivityViewModel()
        sharedPreferenceManager = sharedPreferenceManager(this)
        BottomNavigation()

        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.purple, theme)
        window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.white, theme)

        if(USER_ID!=null){
            productivityViewModel.FetchDayCards(this)
        }


        adapter = DayCardRecyclerAdapter(this, dayCardNameList, productivityViewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        adapter.notifyDataSetChanged()

        binding.progressBar.visibility = View.VISIBLE
        productivityViewModel.dayCardList.observe(this) { list->
            binding.progressBar.visibility = View.GONE
            dayCardNameList.clear()
            dayCardNameList.addAll(list)
            adapter.notifyDataSetChanged()
        }

        binding.floatingActionButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val formattedDate = sdf.format(selectedCalendar.time)

                    if(dayCardNameList.any { it.date == formattedDate }){
                        Toast.makeText(this, "Already Exists", Toast.LENGTH_SHORT).show()
                    }else{
                        productivityViewModel.CreateDayCard(formattedDate, this)
                    }

                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        val googleCardView = findViewById<CardView>(R.id.GoogleCardView)
        googleCardView.setOnClickListener {
            vibrator.vibrate(50)
            googleAuthentication.googleAuth(this)
        }

        // Fetch User Profile Image
        val user = auth.currentUser
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        if(user!=null){
            val photoUrl = user.photoUrl
            if(photoUrl!=null){
                Glide.with(this)
                    .load(photoUrl)
                    .into(profileImage)
            }

        }
    }

    fun BottomNavigation(){
        binding.bottomNavigation.setSelectedItemId(R.id.Productivity_btmNavigation)
        binding.bottomNavigation.setOnItemSelectedListener {

            when(it.itemId){
                R.id.Attendance_btmNavigation ->{
                    vibrator.vibrate(50)
                    sharedPreferenceManager.updateNavigationCode(1)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                R.id.Schedule_btmNavigation ->{
                    vibrator.vibrate(50)
                    sharedPreferenceManager.updateNavigationCode(2)
                    startActivity(Intent(this, Daily_Schedule::class.java))
                    finish()
                }

                R.id.Tasks_btmNavigation ->{
                    vibrator.vibrate(50)
                    sharedPreferenceManager.updateNavigationCode(3)
                    startActivity(Intent(this, TaskActivity::class.java))
                    finish()
                }
            }

            return@setOnItemSelectedListener true
        }
    }

    override fun onResume() {
        super.onResume()
        productivityViewModel.FetchDayCards(this)
        adapter.notifyDataSetChanged()
    }

}