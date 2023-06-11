package com.example.notefirebase.fragments.create.note.and.projects

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateTaskBinding
import com.example.notefirebase.firebasemodel.Task
import com.example.notefirebase.interfaces.TimePickerCallback
import com.example.notefirebase.receivers.AlarmReceiver
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class CreateTaskFragment(
    private val selectedDate: Calendar,
    private val taskUidId: String?,
    private var taskType: Int = 0,
    private var taskRepeat: Int = 0,
    private var taskNotify: Boolean = false,
    private var taskPriority: Int = 0
) : Fragment(), TimePickerCallback {

    private lateinit var fragmentBinding: FragmentCreateTaskBinding
    private lateinit var helper: Helper
    private lateinit var firebaseManager: FirebaseManager
    private var task: List<Task> = emptyList()
    private var taskIsCompleted: Boolean = false
    private var formattedDate: String = ""
    private var currentDate: String = ""
    private var combineCalendar: Calendar? = null
    override fun onTimeSelected(hours: Int, minutes: Int) {

        combineCalendar = Calendar.getInstance()
        combineCalendar!!.apply {
            set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
            set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        helper = Helper(requireActivity())
        firebaseManager = FirebaseManager()
        formattedDate =
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(selectedDate.time)
        currentDate = String.format(
            "%02d-%02d-%d",
            selectedDate.get(Calendar.DAY_OF_MONTH),
            selectedDate.get(Calendar.MONTH) + 1,
            selectedDate.get(Calendar.YEAR)
        )

        fragmentBinding.labelDate.text = formattedDate
        fragmentBinding.taskDate.text = currentDate
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
                    if (it.isNotEmpty()) {
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

//            // Repeat radio buttons
//            when (selectedTask.taskRepeat) {
//                0 -> radioButtonRepeatNever.isChecked = true
//                1 -> radioButtonRepeatEveryDay.isChecked = true
//                2 -> radioButtonRepeatEveryWeek.isChecked = true
//                3 -> radioButtonRepeatEveryMonth.isChecked = true
//            }

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
                helper.navigate(DateNoteFragment(selectedDate))
            }

            // Button commit task
            btnCommitTask.setOnClickListener {
                val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
                val uniqueId: String? = taskUidId ?: databaseReference.push().key

                val taskName = fragmentBinding.inputTaskName.text.toString()
                val taskContent = fragmentBinding.inputTaskContent.text.toString()
                if (taskName == "")
                    helper.customToast(requireContext(), R.string.input_task_name)
                else if (taskContent == "") helper.customToast(
                    requireContext(),
                    R.string.input_task_content
                )
                else {
                    firebaseManager.writeTask(
                        uniqueId!!,
                        taskType,
                        helper.getUid(),
                        taskName,
                        taskContent,
                        currentDate,
                        taskRepeat,
                        taskNotify,
                        taskPriority,
                        taskIsCompleted,
                        combineCalendar?.timeInMillis
                    )

                    if (checkNotification.isChecked) {
                        // Set alarm service
                        val alarmManager =
                            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)
                        alarmIntent.putExtra("selectedDate", selectedDate.timeInMillis)
                        alarmIntent.putExtra("taskName", taskName)
                        alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                        val pendingIntent = PendingIntent.getBroadcast(
                            requireContext(), 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE
                        )

                        combineCalendar?.let { it1 ->
                            alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP, it1.timeInMillis, pendingIntent
                            )
                        }
                    }

                    helper.navigate(DateNoteFragment(selectedDate))
                }
            }
        }
    }

    private fun setUpRadioButton() {
        with(fragmentBinding) {
//            // Repeat radio buttons
//            repeatRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//                when (checkedId) {
//                    // Radio button never repeat is checked
//                    R.id.radioButtonRepeatNever -> {
//                        textTaskRepeat.setText(R.string.radio_button_text_never)
//                        taskRepeat = 0
//                    }
//                    // Radio button repeat every day is checked
//                    R.id.radioButtonRepeatEveryDay -> {
//                        textTaskRepeat.setText(R.string.radio_button_text_every_day)
//                        taskRepeat = 1
//                    }
//                    // Radio button repeat every week is checked
//                    R.id.radioButtonRepeatEveryWeek -> {
//                        textTaskRepeat.setText(R.string.radio_button_text_every_week)
//                        taskRepeat = 2
//                    }
//                    // Radio button repeat every month is checked
//                    R.id.radioButtonRepeatEveryMonth -> {
//                        textTaskRepeat.setText(R.string.radio_button_text_every_month)
//                        taskRepeat = 3
//                    }
//                }
//            }

            // Notification check box
            checkNotification.setOnCheckedChangeListener { _, isChecked ->
                taskNotify = if (isChecked) {
                    // Set text
                    checkNotification.setText(R.string.btn_text_yes)

                    // Check permission for notifications
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        val permissionCheck = PermissionCheckFragment()
                        permissionCheck.show(
                            requireActivity().supportFragmentManager, "GrantPermissionFragment"
                        )
                    } else {
                        val timePickerFragment = TimePickerFragment(selectedDate)
                        timePickerFragment.setTargetFragment(this@CreateTaskFragment, 0)
                        timePickerFragment.show(parentFragmentManager, "TimePickerFragment")
                    }

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