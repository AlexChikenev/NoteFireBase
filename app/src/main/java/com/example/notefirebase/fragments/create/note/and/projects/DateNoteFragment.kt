package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.adapters.TaskAdapter
import com.example.notefirebase.databinding.FragmentDateNoteBinding
import com.example.notefirebase.firebasemodel.Task
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth

class DateNoteFragment(private val selectedDate: String, private val formattedDate: String) : Fragment() {

    private lateinit var fragmentBinding: FragmentDateNoteBinding
    private lateinit var helper: Helper
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var personalTaskAdapter: TaskAdapter
    private lateinit var workTaskAdapter: TaskAdapter
    private val personalTask = 0
    private val workTask = 1

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
        auth = FirebaseAuth.getInstance()
        firebaseManager = FirebaseManager()
        setUpDataBase()
        setupRecyclerView()
        setUpClickListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupRecyclerView() {
        with(fragmentBinding) {
            personalTaskAdapter = TaskAdapter(requireActivity())
            personalAffairsRcView.adapter = personalTaskAdapter
            personalAffairsRcView.layoutManager = LinearLayoutManager(requireContext())

            workTaskAdapter = TaskAdapter(requireActivity())
            workRcView.adapter = workTaskAdapter
            workRcView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpDataBase() {
        val userUid = auth.currentUser?.uid ?: ""
        firebaseManager.getPersonalTask(userUid, {
            personalTaskAdapter.setTask(it, selectedDate)
            Log.d("Personal affairs", "$it")
        }, {
            Log.d("Loading personal", "Error of loading data")
        })

        firebaseManager.getWorkTask(userUid, {
            workTaskAdapter.setTask(it, selectedDate)
            Log.d("Personal affairs", "$it")
        }, {
            Log.d("Loading personal", "Error of loading data")
        })
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
                helper.navigate(CreateTaskFragment(selectedDate, formattedDate, null, personalTask))
            }

            // Create work
            btnCreateWork.setOnClickListener {
                helper.navigate(CreateTaskFragment(selectedDate, formattedDate, null, workTask))
            }

            // Opening personal task for editing
            personalTaskAdapter.setOnClickListener(object : TaskAdapter.OnClickListener {
                override fun onClick(task: Task) {
                    helper.navigate(CreateTaskFragment(selectedDate, formattedDate, task.uniqueId, personalTask))
                }
            })

            // Opening work task for editing
            workTaskAdapter.setOnClickListener(object : TaskAdapter.OnClickListener {
                override fun onClick(task: Task) {
                    helper.navigate(CreateTaskFragment(selectedDate, formattedDate, task.uniqueId, workTask))
                }
            })

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