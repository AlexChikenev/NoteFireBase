package com.example.notefirebase.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.databinding.FragmentMainBinding
import com.example.notefirebase.fragments.create.note.and.projects.DateNoteFragment
import com.example.notefirebase.fragments.create.note.and.projects.DirectoryFragment
import com.example.notefirebase.fragments.finance.FinanceMainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.Helper
import java.util.Calendar


class MainFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentMainBinding
    private lateinit var helper: Helper
    private var selectedDateForAlarm: Calendar? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentMainBinding.inflate(inflater, container, false)
        helper = Helper(requireActivity())
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpDatePick()
        setUpClickListeners()
    }

    private fun setUpDatePick() {
        with(fragmentBinding) {
            // Get date
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val selectedDateFor = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                selectedDateForAlarm = selectedDateFor
            }
        }
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // To date notes
            btnToCurrentDate.setOnClickListener {
                if (selectedDateForAlarm == null) {
                    // Get date
                    selectedDateForAlarm = Calendar.getInstance()
                    helper.navigate(DateNoteFragment(selectedDateForAlarm!!))
                }else{
                    helper.navigate(DateNoteFragment(selectedDateForAlarm!!))
                }
            }

            // Go to finance
            btnToFinance.setOnClickListener {
                helper.navigate(FinanceMainFragment())
            }

            // Got to notes
            btnToNotes.setOnClickListener {
                helper.navigate(DirectoryFragment())
            }

            // Go to settings
            btnToSettings.setOnClickListener {
                activity
                helper.navigate(MainSettingsFragment())
            }
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}