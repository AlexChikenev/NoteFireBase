package com.example.notefirebase.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentMainBinding
import com.example.notefirebase.fragments.create.note.and.projects.DirectoryFragment
import com.example.notefirebase.fragments.finance.FinanceMainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
//            dao.deleteAllFromDirectory()
//            dao.deleteAllFromDProject()
//            dao.deleteAllFromNote()
//            dao.resetInProject()
//            dao.resetInDirectory()
//            dao.resetInNote()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnToFinance.setOnClickListener {
            navigate(FinanceMainFragment())
        }

        binding.btnToNotes.setOnClickListener {
            navigate(DirectoryFragment())

        }

        binding.btnToWishList.setOnClickListener {
            navigate(WishListFragment())
        }

        binding.btnToSettings.setOnClickListener {
            activity
            navigate(MainSettingsFragment())
        }
    }

    private fun navigate(fragment: Fragment) {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentHolder, fragment)
            ?.commit()
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}