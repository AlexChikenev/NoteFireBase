package com.example.notefirebase.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Task
import com.example.notefirebase.fragments.create.note.and.projects.DeleteTaskFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class TaskAdapter(private val activity: FragmentActivity) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var task: List<Task> = emptyList()
    private lateinit var helper: Helper
    private lateinit var firebaseManager: FirebaseManager
    val userUid = FirebaseAuth.getInstance().uid ?: ""
    private val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userUid)
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        helper = Helper(activity)
        return TaskViewHolder(itemView)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val priorityCheckBox: CheckBox = itemView.findViewById(R.id.checkPriority)
        val nameTextView: TextView = itemView.findViewById(R.id.taskName)

        init {
            // Set up text style and checking the completion of the task
            priorityCheckBox.setOnCheckedChangeListener { _, isChecked ->
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val currentTask = task[currentPosition]
                    if (isChecked) {
                        currentTask.taskIsCompleted = true
                        nameTextView.paintFlags =
                            nameTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        currentTask.taskIsCompleted = false
                        nameTextView.paintFlags =
                            nameTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                    updateTaskInDatabase(currentTask)
                }
            }

            // Set long click listener
            itemView.setOnLongClickListener {
                helper.vibrateDevice()
                // Delete selected task
                val selectedTask = task[adapterPosition]
                // Delete selected personal task
                if (selectedTask.taskType == 0) {
                    val deletePersonalTask = DeleteTaskFragment(selectedTask, 0)
                    deletePersonalTask.show(activity.supportFragmentManager, "DeleteSelectedTask")
                }// Delete selected work task
                else {
                    val deleteWorkTask = DeleteTaskFragment(selectedTask, 1)
                    deleteWorkTask.show(activity.supportFragmentManager, "DeleteSelectedTask")
                }
                true
            }
        }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder) {
            val currentTask = task[position]
            // Set up check box background
            when (currentTask.taskPriority) {
                0 -> {
                    priorityCheckBox.setBackgroundResource(R.drawable.custom_no_priority_check_button_background)
                }

                1 -> {
                    priorityCheckBox.setBackgroundResource(R.drawable.custom_small_priority_check_button_background)
                }

                2 -> {
                    priorityCheckBox.setBackgroundResource(R.drawable.custom_middle_priority_check_button_background)
                }

                else -> {
                    priorityCheckBox.setBackgroundResource(R.drawable.custom_main_priority_check_button_background)
                }
            }

            // Set up appearance
            nameTextView.text = currentTask.taskName
            priorityCheckBox.isChecked = currentTask.taskIsCompleted!!

            holder.itemView.setOnClickListener {
                onClickListener?.onClick(currentTask)
            }
        }
    }

    override fun getItemCount(): Int {
        return task.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTask(taskList: List<Task>, targetDate: String) {
        task = taskList.filter { it.taskDate == targetDate }
        notifyDataSetChanged()
    }

    // Update compete state
    private fun updateTaskInDatabase(task: Task) {
        firebaseManager = FirebaseManager()
        if (task.taskType == 0) {
            ref.child("Personal").child(task.uniqueId).child("taskIsCompleted")
                .setValue(task.taskIsCompleted)
        } else {
            ref.child("Work").child(task.uniqueId).child("taskIsCompleted")
                .setValue(task.taskIsCompleted)
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(task: Task)
    }
}
