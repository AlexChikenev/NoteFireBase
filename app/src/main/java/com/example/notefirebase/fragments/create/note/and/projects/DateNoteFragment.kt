package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentDateNoteBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.Helper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateNoteFragment(private val selectedDate: String, private val formattedDate: String) : Fragment() {

    private lateinit var fragmentBinding: FragmentDateNoteBinding
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentDateNoteBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        helper = Helper(requireActivity())
        fragmentBinding.labelDate.text = formattedDate
        setUpClickListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {

            // Go to settings
            btnToSettings.setOnClickListener {
                helper.navigate(MainSettingsFragment())
            }

            // Go to main
            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }

            // Create personal affair
            btnCreatePersonalAffairs.setOnClickListener {
                helper.navigate(CreateTaskFragment(selectedDate, formattedDate))
            }

            // Create work
            btnCreateWork.setOnClickListener {
                helper.navigate(CreateTaskFragment(selectedDate, formattedDate))
            }

            // Create time event
            btnCreateTimeEvent.setOnClickListener {

            }

            // Create habit
            btnCreateHabit.setOnClickListener {

            }
        }
    }

//    companion object {
//        fun newInstance() = DateNoteFragment()
//    }
}