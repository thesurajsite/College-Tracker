package ScheduleRecyclerView

import android.content.Context
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R

// ScheduleItemClickListener interface
interface ScheduleItemClickListener {
    fun onEditScheduleClicked(position: Int)
//    fun onDeleteScheduleClicked(position: Int)
}

class RecyclerScheduleAdapter(val context: Context,
                              val scheduleItemClickListener: ScheduleItemClickListener,
                              val arrSchedule: ArrayList<ScheduleModel>
) : RecyclerView.Adapter<RecyclerScheduleAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        // subject (FROM THE schedule_row LAYOUT)
        val subject = itemView.findViewById<TextView>(R.id.subject)
        val time = itemView.findViewById<TextView>(R.id.time)

        //VIBRATOR VIBRATOR VIBRATOR
        val vibrator = itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.schedule_row, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return arrSchedule.size


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // This Links the TextView of the Schedule Row with the arrSchedule Array
        holder.subject.text = arrSchedule[position].subject
        holder.time.text = arrSchedule[position].time

        holder.itemView.setOnClickListener {
            scheduleItemClickListener.onEditScheduleClicked(position)
        }


    }
}



