package Adapters

import Models.TaskDataClass
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R
import com.google.android.gms.tasks.Task

class RecyclerTaskAdapter(private val context: Context, val listener: TaskClickListener): RecyclerView.Adapter<RecyclerTaskAdapter.TaskViewHolder>() {

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

        holder.task_layout.setOnClickListener {
            listener.onItemClicked(TaskList[holder.adapterPosition])
        }

        holder.task_layout.setOnLongClickListener {
            listener.onLongItemClicked(TaskList[holder.adapterPosition], holder.task_layout)
            true
        }
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val task_layout=itemView.findViewById<CardView>(R.id.taskRow)
        val taskName=itemView.findViewById<TextView>(R.id.taskName)


    }

    interface TaskClickListener{
        fun onItemClicked(task: TaskDataClass)
        fun onLongItemClicked(task: TaskDataClass, taskRow: CardView)
    }
}