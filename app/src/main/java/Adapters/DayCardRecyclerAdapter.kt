package Adapters

import Activities.AddTasksDayCardActivity
import Adapters.RecyclerAttendanceAdapter.ViewHolder
import Models.ProductivityViewModel
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class DayCardRecyclerAdapter(val context: Context, val dayCardList: List<String>, private val productivityViewModel: ProductivityViewModel) : RecyclerView.Adapter<DayCardRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)
        val cardView = itemView.findViewById<CardView>(R.id.cardView)
        val progressBar=itemView.findViewById<CircularProgressBar>(R.id.circularProgressBar)
        val percentTextView = itemView.findViewById<TextView>(R.id.percentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.day_card_layout, parent, false))
    }

    override fun onBindViewHolder(holder: DayCardRecyclerAdapter.ViewHolder, position: Int) {
        holder.dateTextView.text = dayCardList[position]
        val date = dayCardList[position]

        holder.cardView.setOnClickListener {
            val intent = Intent(context, AddTasksDayCardActivity::class.java)
            intent.putExtra("date", dayCardList[position])
            context.startActivity(intent)
        }

        holder.progressBar.apply {
            productivityViewModel.fetchCompletePercentForDayCard(date){ percent->
                setProgressWithAnimation(percent.toFloat(), 1000)
                holder.percentTextView.text = percent.toInt().toString()+"%"

                productivityViewModel.fetchCompletePercentForDayCard(date) { percent ->
                    val colorRes = if (percent < 60) R.color.red_color
                    else R.color.dark_purple
                    holder.progressBar.apply {
                        setProgressWithAnimation(percent.toFloat(), 1000)
                        progressBarColor = ContextCompat.getColor(context, colorRes)
                        roundBorder = true
                    }
                    holder.percentTextView.text = "$percent%"
                }

            }

            roundBorder=true

        }
    }

    override fun getItemCount(): Int {
        return dayCardList.size
    }
}