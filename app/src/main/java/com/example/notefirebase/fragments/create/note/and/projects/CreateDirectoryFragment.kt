package com.example.notefirebase.fragments.create.note.and.projects

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateDirectoryBinding
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper

class CreateDirectoryFragment : DialogFragment() {

    private lateinit var fragmentBinding: FragmentCreateDirectoryBinding
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var helper: Helper

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateDirectoryBinding.inflate(inflater, container, false)
        firebaseManager = FirebaseManager()
        helper = Helper(requireActivity())
        val decorView = dialog?.window!!.decorView
        helper.uiControls(decorView)
        return fragmentBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            // Button of creation new directory
            btnCommitDirectory.setOnClickListener {
                if (inputDirectoryName.text.toString().isNotBlank()) {
                    val directoryName = inputDirectoryName.text.toString()
                    firebaseManager.writeDirectory(helper.getUid(), directoryName)
                    dismiss()
                } else {
                    helper.customToast(requireContext(), R.string.enter_directory_name)
                }
            }
        }
    }

    // Parameters for this fragment
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val backgroundColor = ContextCompat.getColor(requireContext(), R.color.white_29)
            setBackgroundDrawable(ColorDrawable(backgroundColor))
            setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
    }
}