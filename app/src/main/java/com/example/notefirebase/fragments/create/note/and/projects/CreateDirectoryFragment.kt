package com.example.notefirebase.fragments.create.note.and.projects

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateDirectoryBinding
import com.example.notefirebase.utils.FirebaseManager
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class CreateDirectoryFragment : DialogFragment() {

    private lateinit var fragmentBinding: FragmentCreateDirectoryBinding
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var userUid: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateDirectoryBinding.inflate(inflater, container, false)
        firebaseManager = FirebaseManager()
        userUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    private fun setupClickListeners() {
        with(fragmentBinding) {
            // Button of creation new directory
            btnCommitDirectory.setOnClickListener {
                if (inputDirectoryName.text.toString().isNotBlank()) {
                    val directoryId = UUID.randomUUID().toString()
                    val userUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                    val directoryName = inputDirectoryName.text.toString()
                    firebaseManager.writeDirectory(directoryId, userUid, directoryName)
                    dismiss()
                } else {
                    Toast.makeText(context, R.string.enter_directory_name, Toast.LENGTH_SHORT)
                        .show()
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