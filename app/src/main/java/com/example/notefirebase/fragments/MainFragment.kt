package com.example.notefirebase.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import com.example.notefirebase.utils.Helper
import android.view.ViewGroup
import com.example.notefirebase.databinding.FragmentMainBinding
import com.example.notefirebase.fragments.create.note.and.projects.DateNoteFragment
import com.example.notefirebase.fragments.create.note.and.projects.DirectoryFragment
import com.example.notefirebase.fragments.finance.FinanceMainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentMainBinding
    private lateinit var helper: Helper
    private var selectedDate: String = ""
    private var formattedDate: String = ""

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
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                    Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }.time
                )
                selectedDate = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year)
            }
        }
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {

            // To date notes
            btnToCurrentDate.setOnClickListener {
                if(selectedDate == ""){
                    val currentDate = Calendar.getInstance().time
                    selectedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(currentDate)
                    formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(currentDate)
                }
                    helper.navigate(DateNoteFragment(selectedDate, formattedDate))
            }

            // Go to finance
            btnToFinance.setOnClickListener {
                helper.navigate(FinanceMainFragment())
            }

            // Got to notes
            btnToNotes.setOnClickListener {
                helper.navigate(DirectoryFragment())
            }
//
//            // Go to wish list
//            btnToWishList.setOnClickListener {
//                helper.navigate(WishListFragment())
//            }

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