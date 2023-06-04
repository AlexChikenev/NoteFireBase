package com.example.notefirebase.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import com.example.notefirebase.utils.Helper
import android.view.ViewGroup
import com.example.notefirebase.databinding.FragmentMainBinding
import com.example.notefirebase.fragments.create.note.and.projects.DirectoryFragment
import com.example.notefirebase.fragments.finance.FinanceMainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment


class MainFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentMainBinding
    private lateinit var helper: Helper

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
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
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