package Adapters

import Models.DayTask
import Models.ProductivityViewModel
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R

class DayTaskRecyclerAdapter(val context: Context, val dayTaskList: List<DayTask>, private val productivityViewModel: ProductivityViewModel) : RecyclerView.Adapter<DayTaskRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val taskRow = itemView.findViewById<CardView>(R.id.taskRow)
        val  taskName = itemView.findViewById<TextView>(R.id.taskName)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox)
        val vibrator = itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_daytask, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.taskName.text = dayTaskList[position].taskName
        holder.checkBox.isChecked = dayTaskList[position].complete!!

        val date = dayTaskList[position].date

        holder.taskRow.setOnClickListener {
            holder.vibrator.vibrate(50)
            holder.checkBox.isChecked = !holder.checkBox.isChecked
            val updatedTask = DayTask(
                dayTaskList[position].taskId,
                dayTaskList[position].taskName,
                holder.checkBox.isChecked,
                dayTaskList[position].time,
                dayTaskList[position].date
            )
            productivityViewModel.UpdateTask(date!!, updatedTask, context)
        }

        holder.taskRow.setOnLongClickListener {
            holder.vibrator.vibrate(50)
            val builder = AlertDialog.Builder(context)
                .setTitle("Delete Task")
                .setIcon(R.drawable.baseline_delete_24)
                .setMessage("Do you want to Delete this Task ?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->
                    holder.vibrator.vibrate(50)
                    try {
                        productivityViewModel.DeleteTask(dayTaskList[position].date.toString(), dayTaskList[position].taskId.toString(), context)

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

        holder.checkBox.setOnClickListener {
            val updatedTask = DayTask(
                dayTaskList[position].taskId,
                dayTaskList[position].taskName,
                holder.checkBox.isChecked,
                dayTaskList[position].time,
                dayTaskList[position].date
            )
            productivityViewModel.UpdateTask(date!!, updatedTask, context)
        }
    }

    override fun getItemCount(): Int {
        return dayTaskList.size
    }
}