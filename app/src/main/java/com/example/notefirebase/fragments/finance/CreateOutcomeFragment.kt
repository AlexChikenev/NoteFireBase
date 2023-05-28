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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateOutcomeBinding
import com.example.notefirebase.utils.FirebaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateOutcomeFragment : DialogFragment() {

    private lateinit var fragmentBinding: FragmentCreateOutcomeBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseManager: FirebaseManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateOutcomeBinding.inflate(inflater, container, false)
        databaseReference = FirebaseDatabase.getInstance().reference
        return fragmentBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseManager = FirebaseManager()
        auth = FirebaseAuth.getInstance()
        setUpClickListeners()
        setUpEditText()

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
    }

    // Replace "," to "."
    private fun setUpEditText() {
        with(fragmentBinding) {
            inputOutcome.addTextChangedListener(object : TextWatcher {
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
                            val selectionStart = inputOutcome.selectionStart
                            val selectionEnd = inputOutcome.selectionEnd

                            inputOutcome.setText(modifiedText)
                            inputOutcome.setSelection(selectionStart, selectionEnd)
                        }
                    }
                }
            })
        }
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            btnCommitOutcome.setOnClickListener {
                val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val outcome = inputOutcome.text.toString()
                val outcomeName = inputIncomeName.text.toString()
                if (outcomeName.isEmpty()) {
                    Toast.makeText(
                        requireContext(), "Введите название расхода", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    try {
                        firebaseManager.writeOutcome(outcome.toDouble(), outcomeName, userUid)
                        dismiss()
                    } catch (exception: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Введите числовое значение в поле расходов",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

    companion object {
        fun newInstance() = CreateOutcomeFragment()
    }
}