package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notefirebase.adapters.DirectoryAdapter
import com.example.notefirebase.databinding.FragmentDirectoryBinding
import com.example.notefirebase.firebasemodel.Directory
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth


class DirectoryFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentDirectoryBinding
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var adapter: DirectoryAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentDirectoryBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseManager = FirebaseManager()
        helper = Helper(requireActivity())
        auth = FirebaseAuth.getInstance()
        setupRecyclerView()
        setupDatabase()
        setUpClickListeners()
    }

    // Set Up RecyclerView
    private fun setupRecyclerView() {
        adapter = DirectoryAdapter(requireActivity(), requireContext())
        fragmentBinding.rcDirectory.adapter = adapter
        fragmentBinding.rcDirectory.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    // Loading data from the database
    private fun setupDatabase() {
        firebaseManager.getDirectory(helper.getUid(), {
            adapter.setDirectories(it)
        }, {
            Log.d("Directory error", "Не удалось загрузить данные")
        })
    }

    // Set up click listeners
    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Create directory
            btnCreateDirectory.setOnClickListener {
                val createDirectory = CreateDirectoryFragment()
                createDirectory.show(
                    requireActivity().supportFragmentManager, "CreateDirectoryFragment"
                )
            }

            // Click on item
            adapter.setOnClickListener(object : DirectoryAdapter.OnClickListener {
                override fun onClick(directory: Directory) {
                    helper.navigate(
                        OpenDirectoryFragment(
                            directory.directoryUniqueId,
                            directory.directoryName
                        )
                    )
                }
            })

            // To main
            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }

            // To settings
            btnToSettings.setOnClickListener {
                helper.navigate(MainSettingsFragment())
            }
        }
    }

    companion object {
        fun newInstance() = DirectoryFragment()
    }
}