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
import android.view.ViewGroup.LayoutParams
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateIncomeBinding
import com.example.notefirebase.firebasemodel.FirebaseIncomes
import com.example.notefirebase.utils.FirebaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateIncomeFragment : DialogFragment() {
    private lateinit var fragmentBinding: FragmentCreateIncomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseManager: FirebaseManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateIncomeBinding.inflate(inflater, container, false)
        firebaseManager = FirebaseManager()
        return fragmentBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
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

    // Replace "," to "."
    private fun setUpEditText() {
        with(fragmentBinding) {
            inputIncome.addTextChangedListener(object : TextWatcher {
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
                            val selectionStart = inputIncome.selectionStart
                            val selectionEnd = inputIncome.selectionEnd

                            inputIncome.setText(modifiedText)
                            inputIncome.setSelection(selectionStart, selectionEnd)
                        }
                    }
                }
            })
        }

    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            btnCommitIncome.setOnClickListener {
                val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val income = inputIncome.text.toString()
                val incomeName = inputIncomeName.text.toString()
                if (incomeName.isEmpty()) {
                    Toast.makeText(
                        requireContext(), "Введите название дохода", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    try {
                        firebaseManager.writeIncome(income.toDouble(), incomeName, userUid)
                        dismiss()
                    } catch (exception: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Введите числовое значение в поле доходов",
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
            setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
    }

    companion object {
        fun newInstance() = CreateIncomeFragment()
    }
}