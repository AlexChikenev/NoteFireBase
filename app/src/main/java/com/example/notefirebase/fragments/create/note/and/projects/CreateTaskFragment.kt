package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateTaskBinding
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth

class CreateTaskFragment(
    private var selectedDate: String,
    private val formattedDate: String,
    private var taskType: Int = 0,
    private var taskRepeat: Int = 0,
    private var taskNotify: Boolean = false,
    private var taskPriority: Int = 0
) :
    Fragment() {

    private lateinit var fragmentBinding: FragmentCreateTaskBinding
    private lateinit var helper: Helper
    private lateinit var firebaseManager: FirebaseManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Go back
            btnGoBack.setOnClickListener {
                helper.navigate(DateNoteFragment(selectedDate, formattedDate))
            }

            // Button commit task
            btnCommitTask.setOnClickListener {
                val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val taskName = fragmentBinding.inputTaskName.text.toString()
                val taskContent = fragmentBinding.inputTaskContent.text.toString()
                if (taskName == "")
                    helper.customToast(requireContext(), R.string.input_task_name)
                else if (taskContent == "")
                    helper.customToast(requireContext(), R.string.input_task_content)
                else
                    firebaseManager.writeTask(
                        taskType,
                        userUid,
                        taskName,
                        taskContent,
                        selectedDate,
                        taskRepeat,
                        taskNotify,
                        taskPriority
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
                    R.id.radioNoPriority -> taskPriority = 0
                    // Radio button small priority
                    R.id.radioSmallPriority -> taskPriority = 1
                    // Radio button middle priority
                    R.id.radioMiddlePriority -> taskPriority = 2
                    // Radio button main priority
                    R.id.radioMainPriority -> taskPriority = 3
                }
            }
        }
    }



//    companion object {
//        fun newInstance() = CreateTaskFragment()
//    }
}