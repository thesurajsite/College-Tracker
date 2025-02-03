package Adapters

import Activities.TaskActivity
import Models.TaskDataClass
import Models.TaskViewModel
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R
import com.google.android.gms.tasks.Task

class RecyclerTaskAdapter(private val context: Context, val listener: TaskClickListener, private val viewModel: TaskViewModel): RecyclerView.Adapter<RecyclerTaskAdapter.TaskViewHolder>() {

    private val TaskList:ArrayList<TaskDataClass> = ArrayList()
    private val FullList: ArrayList<TaskDataClass> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        return TaskViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return TaskList.size
    }

    fun updateList(newList: List<TaskDataClass>){
        FullList.clear()
        FullList.addAll(newList)

        TaskList.clear()
        TaskList.addAll(FullList)

        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {


        val currentTask=TaskList[position]
        holder.taskName.text=currentTask.taskName
        holder.taskName.isSelected= true


        if(currentTask.isComplete == true){
            holder.checkbox.isChecked=true
            holder.taskName.paintFlags = holder.taskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        else{
            holder.checkbox.isChecked=false
            holder.taskName.paintFlags = holder.taskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }


        if(currentTask.priority=="Low")
            holder.task_layout.backgroundTintList = ContextCompat.getColorStateList(context, R.color.light_green)
        else if(currentTask.priority=="Medium")
            holder.task_layout.backgroundTintList = ContextCompat.getColorStateList(context, R.color.light_yellow)
        else if(currentTask.priority=="High")
            holder.task_layout.backgroundTintList = ContextCompat.getColorStateList(context, R.color.light_red)
        else holder.task_layout.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)


        holder.task_layout.setOnClickListener {
            listener.onItemClicked(TaskList[holder.adapterPosition])
        }

        
        holder.checkbox.setOnClickListener {
            holder.vibrator.vibrate(50)
            if(holder.checkbox.isChecked){
                viewModel.updateTask(TaskDataClass(currentTask.id, currentTask.taskName, currentTask.priority, currentTask.taskDetails, true))
                holder.taskName.paintFlags = holder.taskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            else{
                viewModel.updateTask(TaskDataClass(currentTask.id, currentTask.taskName, currentTask.priority, currentTask.taskDetails, false))
                holder.taskName.paintFlags = holder.taskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

        }

        holder.task_layout.setOnLongClickListener {
            holder.vibrator.vibrate(50)

            val builder = AlertDialog.Builder(context)
                .setTitle("Delete Task")
                .setIcon(R.drawable.baseline_delete_24)
                .setMessage("Do you want to Delete this Task ?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->
                    try {

                        holder.vibrator.vibrate(50)
                        Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show()
                        val task = TaskDataClass(currentTask.id, currentTask.taskName, currentTask.priority, currentTask.taskDetails, currentTask.isComplete)
                        viewModel.deleteTask(task)


                    } catch (e: Exception) {
                        Toast.makeText(context, "Something Went wrong", Toast.LENGTH_SHORT).show()
                        Log.w("crash-delete", e)
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

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val task_layout=itemView.findViewById<CardView>(R.id.taskRow)
        val taskName=itemView.findViewById<TextView>(R.id.taskName)
        val checkbox= itemView.findViewById<CheckBox>(R.id.checkbox)
        val vibrator = itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }

    interface TaskClickListener{
        fun onItemClicked(task: TaskDataClass)
        fun onLongItemClicked(task: TaskDataClass, taskRow: CardView)
    }
}