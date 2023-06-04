package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateTaskBinding
import com.example.notefirebase.utils.Helper

class CreateTaskFragment(private var selectedDate: String, private val formattedDate: String) :
    Fragment() {

    private lateinit var fragmentBinding: FragmentCreateTaskBinding
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        helper = Helper(requireActivity())
        fragmentBinding.labelDate.text = formattedDate
        fragmentBinding.taskDate.text = selectedDate
        setUpRadioButton()
        setUpClickListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            btnToMain.setOnClickListener {
                helper.navigate(DateNoteFragment(selectedDate, formattedDate))
            }
        }
    }

    private fun setUpRadioButton() {
        with(fragmentBinding) {
            repeatRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId){
                    // Radio button never repeat is checked
                    R.id.radioButtonRepeatNever -> taskRepeat.setText(R.string.radio_button_text_never)
                    // Radio button repeat every day is checked
                    R.id.radioButtonRepeatEveryDay -> taskRepeat.setText(R.string.radio_button_text_every_day)
                    // Radio button repeat every week is checked
                    R.id.radioButtonRepeatEveryWeek -> taskRepeat.setText(R.string.radio_button_text_every_week)
                    // Radio button repeat every month is checked
                    R.id.radioButtonRepeatEveryMonth -> taskRepeat.setText(R.string.radio_button_text_every_month)
                }
            }
        }
    }



//    companion object {
//        fun newInstance() = CreateTaskFragment()
//    }
}