package com.example.notefirebase.fragments.create.note.and.projects

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.databinding.FragmentCreateDirectoryBinding
import com.example.notefirebase.firebasemodel.FirebaseDirectory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class CreateDirectoryFragment() : DialogFragment() {

    private lateinit var binding: FragmentCreateDirectoryBinding
    private lateinit var gestureDetector: GestureDetector
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userUid: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateDirectoryBinding.inflate(inflater, container, false)

        gestureDetector =
            GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (e1 != null && e2 != null) {
                        if (e1.y < e2.y && velocityY > 500)
                            dismiss()
                    }
                    return false
                }
            })

        binding.topBar.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseReference = FirebaseDatabase.getInstance().reference
        // Button of creation new directory
        binding.btnCommitDir.setOnClickListener {
            if (binding.editTextDirectoryName.text.toString() != "") {

                val directoryId = UUID.randomUUID().toString()
                val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//                val email = FirebaseAuth.getInstance().currentUser?.email
//                val parts = email?.split("@")
//                val userEmail = parts!![0]
                val directoryName = binding.editTextDirectoryName.text.toString()


                writeDirectory(directoryId, userUid, directoryName)
                dismiss()
            } else {
                Toast.makeText(context, "Введите название новой папки", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun writeDirectory(directoryId: String, userUid: String, name: String) {
        val directory = FirebaseDirectory(directoryId, userUid, name)

        databaseReference.child("Users").child(userUid).child("Directory").child(name).setValue(directory)
    }

    // Parameters for this fragment
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, 1000)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }
}