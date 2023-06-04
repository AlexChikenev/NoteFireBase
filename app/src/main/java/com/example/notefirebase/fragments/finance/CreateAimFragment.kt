package com.example.notefirebase.fragments.finance

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreatePillowBinding
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateAimFragment : DialogFragment() {
    private lateinit var fragmentBinding: FragmentCreatePillowBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var auth: FirebaseAuth
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreatePillowBinding.inflate(inflater, container, false)
        helper = Helper(requireActivity())
        val decorView: View = dialog?.window!!.decorView
        helper.uiControls(decorView)
        return fragmentBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseManager = FirebaseManager()
        setUpClickListeners()
        setUpEditText()
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

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            btnCommitAim.setOnClickListener {
                val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val aim = inputAim.text.toString()
                if (aim.isEmpty()) {
                    helper.customToast(requireContext(), R.string.input_aim)
                } else {
                    try {
                        firebaseManager.writeAim(aim.toDouble(), userUid)
                        dismiss()
                    } catch (exception: Exception) {
                        helper.customToast(requireContext(), R.string.input_number)
                    }
                }
            }
        }
    }

    // Replace "," to "."
    private fun setUpEditText() {
        with(fragmentBinding) {
            inputAim.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val text = s?.toString()
                    text?.let {
                        val modifiedText = it.replace(",", ".")
                        if (modifiedText != text) {
                            val selectionStart = inputAim.selectionStart
                            val selectionEnd = inputAim.selectionEnd

                            inputAim.setText(modifiedText)
                            inputAim.setSelection(selectionStart, selectionEnd)
                        }
                    }
                }
            })
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

    companion object {
        fun newInstance() = CreateAimFragment()
    }
}