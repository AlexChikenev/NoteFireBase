package com.example.notefirebase.fragments.create.note.and.projects

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentPermissionCheckBinding
import com.example.notefirebase.utils.Helper

class PermissionCheckFragment :
    DialogFragment() {

    private lateinit var fragmentBinding: FragmentPermissionCheckBinding
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentPermissionCheckBinding.inflate(inflater, container, false)
        helper = Helper(requireActivity())
        val decorView = dialog?.window!!.decorView
        helper.uiControls(decorView)
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
        setUpClickListeners()
    }

    // Set up click listeners
    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Commit event
            btnCommit.setOnClickListener {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, "com.example.notefirebase")
                startActivity(intent)
                dismiss()
            }
            // Decline deletion
            btnDecline.setOnClickListener {
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