package com.example.notefirebase.fragments.create.note.and.projects

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateTimeEventBinding
import com.example.notefirebase.receivers.AlarmReceiver
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import java.util.Calendar

class CreateTimeEventFragment(private val selectedDate: Calendar) : DialogFragment() {

    private lateinit var fragmentBinding: FragmentCreateTimeEventBinding
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var helper: Helper
    private var currentDate: String = ""
    private var combineCalendar: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateTimeEventBinding.inflate(inflater, container, false)
        firebaseManager = FirebaseManager()
        helper = Helper(requireActivity())
        val decorView = dialog?.window!!.decorView
        fragmentBinding.timePicker.setIs24HourView(true)
        helper.uiControls(decorView)
        return fragmentBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentDate = String.format(
            "%02d-%02d-%d",
            selectedDate.get(Calendar.DAY_OF_MONTH),
            selectedDate.get(Calendar.MONTH) + 1,
            selectedDate.get(Calendar.YEAR)
        )

        setupClickListeners()
        fragmentBinding.dialogBackground.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dismiss()
                    false
                }

                else -> false
            }
        }
    }

    private fun setupClickListeners() {
        with(fragmentBinding) {
            btnCommit.setOnClickListener {

                val hours = timePicker.hour
                val minutes = timePicker.minute

                combineCalendar = Calendar.getInstance()
                combineCalendar!!.apply {
                    set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
                    set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, hours)
                    set(Calendar.MINUTE, minutes)
                    set(Calendar.SECOND, 0)
                }

                val time = "$hours:$minutes  "

                if (inputTimeEventName.text.isNotEmpty()) {
                    val eventName = time + inputTimeEventName.text.toString()
                    firebaseManager.writeTimeEvent(
                        helper.getUid(),
                        eventName,
                        currentDate,
                        false,
                        combineCalendar!!.timeInMillis
                    )
                    // Set alarm service
                    val alarmManager =
                        requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)
                    alarmIntent.putExtra("selectedDate", selectedDate.timeInMillis)
                    alarmIntent.putExtra("taskName", eventName)
                    alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    val pendingIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        0,
                        alarmIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )

                    combineCalendar?.let { it1 ->
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            it1.timeInMillis,
                            pendingIntent
                        )
                    }
                    dismiss()
                } else {
                    helper.customToast(requireContext(), R.string.input_event_name)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val backgroundColor = ContextCompat.getColor(requireContext(), R.color.white_29)
            setBackgroundDrawable(ColorDrawable(backgroundColor))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}