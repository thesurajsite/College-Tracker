package Adapters

import Activities.AddTasksDayCardActivity
import Activities.MainActivity
import Adapters.RecyclerAttendanceAdapter.ViewHolder
import Models.DayCard
import Models.ProductivityViewModel
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class DayCardRecyclerAdapter(val context: Context, val dayCardList: List<DayCard>, private val productivityViewModel: ProductivityViewModel) : RecyclerView.Adapter<DayCardRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)
        val cardView = itemView.findViewById<CardView>(R.id.cardView)
        val progressBar=itemView.findViewById<CircularProgressBar>(R.id.circularProgressBar)
        val percentTextView = itemView.findViewById<TextView>(R.id.percentTextView)
        val vibrator = itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.day_card_layout, parent, false))
    }

    override fun onBindViewHolder(holder: DayCardRecyclerAdapter.ViewHolder, position: Int) {
        holder.dateTextView.text = dayCardList[position].date
        val date = dayCardList[position].date.toString()

        holder.cardView.setOnClickListener {
            holder.vibrator.vibrate(50)
            val intent = Intent(context, AddTasksDayCardActivity::class.java)
            intent.putExtra("date", dayCardList[position].date)
            context.startActivity(intent)
        }

        holder.percentTextView.text = dayCardList[position].percentage+"%" ?: "0%"
        holder.progressBar.apply {
            val percentageStr = dayCardList[position].percentage ?: "0"
            val percent = percentageStr.toIntOrNull() ?: 0

            setProgressWithAnimation(percent.toFloat(), 1000)
            progressBarColor = ContextCompat.getColor(
                context,
                if (percent < 70) R.color.red_color else R.color.dark_purple
            )
            roundBorder = true
        }


        holder.cardView.setOnLongClickListener {
            holder.vibrator.vibrate(50)
            val builder = AlertDialog.Builder(context)
                .setTitle("Delete DayCard")
                .setIcon(R.drawable.baseline_delete_24)
                .setMessage("Do you want to Delete this DayCard ?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->
                    holder.vibrator.vibrate(50)
                    try {
                        productivityViewModel.DeleteDayCard(dayCardList[position].date.toString(), context)

                    } catch (e: Exception) {
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        Log.w("crash-attendance", e)
                    }

                }.setNegativeButton("No")
                { dialogInterface, i ->
                    holder.vibrator.vibrate(50)
                    Toast.makeText(context, "Deletion Cancelled", Toast.LENGTH_SHORT).show()
                }
            builder.show()

            true
        }
    }

    override fun getItemCount(): Int {
        return dayCardList.size
    }
}