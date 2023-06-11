package com.example.notefirebase.fragments.create.note.and.projects

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentTimePickerBinding
import com.example.notefirebase.interfaces.TimePickerCallback
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import java.util.Calendar

@Suppress("DEPRECATION")
class TimePickerFragment(private val selectedDate: Calendar) : DialogFragment() {

    private lateinit var fragmentBinding: FragmentTimePickerBinding
    private lateinit var helper: Helper
    private lateinit var firebaseManager: FirebaseManager
    private var callback: TimePickerCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TimePickerCallback) {
            callback = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentTimePickerBinding.inflate(inflater, container, false)
        firebaseManager = FirebaseManager()
        helper = Helper(requireActivity())
        val decorView = dialog?.window!!.decorView
        helper.uiControls(decorView)
        fragmentBinding.timePicker.setIs24HourView(true)
        return fragmentBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBinding.dialogBackground.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dismiss()
                    false
                }

                else -> false
            }
        }
        setUpClickListener()
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    private fun setUpClickListener() {
        with(fragmentBinding) {
            btnCommit.setOnClickListener {

                // Get hour and minute
                val hour = timePicker.hour
                val minute = timePicker.minute

                // Combining date and time
                val calendar = Calendar.getInstance()
                calendar.apply {
                    set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
                    set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }

                val targetFragment = targetFragment as? CreateTaskFragment
                targetFragment?.onTimeSelected(hour, minute)

                dismiss()
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