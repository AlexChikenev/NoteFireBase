package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateTaskBinding
import com.example.notefirebase.firebasemodel.Task
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateTaskFragment(
    private var selectedDate: String,
    private val formattedDate: String,
    private val taskUidId: String?,
    private var taskType: Int = 0,
    private var taskRepeat: Int = 0,
    private var taskNotify: Boolean = false,
    private var taskPriority: Int = 0
) :
    Fragment() {

    private lateinit var fragmentBinding: FragmentCreateTaskBinding
    private lateinit var helper: Helper
    private lateinit var firebaseManager: FirebaseManager
    private var task: List<Task> = emptyList()
    private var taskIsCompleted: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        helper = Helper(requireActivity())
        firebaseManager = FirebaseManager()
        fragmentBinding.labelDate.text = formattedDate
        fragmentBinding.taskDate.text = selectedDate
        setUpRadioButton()
        setUpClickListeners()
        setUpDatabase()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpDatabase() {
        // Get personal task
        if (taskType == 0) {
            if (taskUidId != null) {
                firebaseManager.getPersonalTaskForEdit(helper.getUid(), taskUidId, {
                    if(it.isNotEmpty()){
                        task = it
                        taskIsCompleted = task[0].taskIsCompleted!!
                        setUpUi(task[0])
                    }
                }, {})
            }
        } // Get work task
        else {
            if (taskUidId != null) {
                firebaseManager.getWorkTaskForEdit(helper.getUid(), taskUidId, {
                    if(it.isNotEmpty()){
                        task = it
                        taskIsCompleted = task[0].taskIsCompleted!!
                        setUpUi(task[0])
                    }
                }, {})
            }
        }
    }

    // Set up task data
    private fun setUpUi(selectedTask: Task) {
        with(fragmentBinding) {
            inputTaskName.setText(selectedTask.taskName ?: "")
            inputTaskContent.setText(selectedTask.taskContent ?: "")

            // Repeat radio buttons
            when (selectedTask.taskRepeat) {
                0 -> radioButtonRepeatNever.isChecked = true
                1 -> radioButtonRepeatEveryDay.isChecked = true
                2 -> radioButtonRepeatEveryWeek.isChecked = true
                3 -> radioButtonRepeatEveryMonth.isChecked = true
            }

            // Notification check button
            checkNotification.isChecked = selectedTask.taskNotification != false

            // Priority radio buttons
            when (selectedTask.taskPriority) {
                0 -> radioButtonNoPriority.isChecked = true
                1 -> radioButtonSmallPriority.isChecked = true
                2 -> radioButtonMiddlePriority.isChecked = true
                3 -> radioButtonMainPriority.isChecked = true
            }
        }
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Go back
            btnGoBack.setOnClickListener {
                helper.navigate(DateNoteFragment(selectedDate, formattedDate))
            }

            // Button commit task
            btnCommitTask.setOnClickListener {
                val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
                val uniqueId: String? = taskUidId ?: databaseReference.push().key

                val taskName = fragmentBinding.inputTaskName.text.toString()
                val taskContent = fragmentBinding.inputTaskContent.text.toString()
                if (taskName == "")
                    helper.customToast(requireContext(), R.string.input_task_name)
                else if (taskContent == "")
                    helper.customToast(requireContext(), R.string.input_task_content)
                else
                    firebaseManager.writeTask(
                        uniqueId!!,
                        taskType,
                        helper.getUid(),
                        taskName,
                        taskContent,
                        selectedDate,
                        taskRepeat,
                        taskNotify,
                        taskPriority,
                        taskIsCompleted
                    )
            }
        }
    }

    private fun setUpRadioButton() {
        with(fragmentBinding) {
            // Repeat radio buttons
            repeatRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    // Radio button never repeat is checked
                    R.id.radioButtonRepeatNever -> {
                        textTaskRepeat.setText(R.string.radio_button_text_never)
                        taskRepeat = 0
                    }
                    // Radio button repeat every day is checked
                    R.id.radioButtonRepeatEveryDay -> {
                        textTaskRepeat.setText(R.string.radio_button_text_every_day)
                        taskRepeat = 1
                    }
                    // Radio button repeat every week is checked
                    R.id.radioButtonRepeatEveryWeek -> {
                        textTaskRepeat.setText(R.string.radio_button_text_every_week)
                        taskRepeat = 2
                    }
                    // Radio button repeat every month is checked
                    R.id.radioButtonRepeatEveryMonth -> {
                        textTaskRepeat.setText(R.string.radio_button_text_every_month)
                        taskRepeat = 3
                    }
                }
            }

            // Notification check box
            checkNotification.setOnCheckedChangeListener { _, isChecked ->
                taskNotify = if(isChecked){
                    checkNotification.setText(R.string.btn_text_yes)
                    true
                }else{
                    checkNotification.setText(R.string.btn_text_no)
                    false
                }
            }

            // Priority radio buttons
            radioRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    // Radio button no priority
                    R.id.radioButtonNoPriority -> taskPriority = 0
                    // Radio button small priority
                    R.id.radioButtonSmallPriority -> taskPriority = 1
                    // Radio button middle priority
                    R.id.radioButtonMiddlePriority -> taskPriority = 2
                    // Radio button main priority
                    R.id.radioButtonMainPriority -> taskPriority = 3
                }
            }
        }
    }
}