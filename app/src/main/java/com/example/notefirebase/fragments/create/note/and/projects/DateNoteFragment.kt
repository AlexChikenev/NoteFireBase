package com.example.notefirebase.fragments.create.note.and.projects

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.adapters.TaskAdapter
import com.example.notefirebase.adapters.TimeEventAdapter
import com.example.notefirebase.databinding.FragmentDateNoteBinding
import com.example.notefirebase.firebasemodel.Task
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateNoteFragment(private val selectedDate: Calendar) : Fragment() {

    private lateinit var fragmentBinding: FragmentDateNoteBinding
    private lateinit var helper: Helper
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var personalTaskAdapter: TaskAdapter
    private lateinit var workTaskAdapter: TaskAdapter
    private lateinit var timeEventAdapter: TimeEventAdapter
    private var formattedDate: String = ""
    private var currentDate: String = ""
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
        // Formatting date
        formattedDate =
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(selectedDate.time)
        currentDate = String.format(
            "%02d-%02d-%d",
            selectedDate.get(Calendar.DAY_OF_MONTH),
            selectedDate.get(Calendar.MONTH) + 1,
            selectedDate.get(Calendar.YEAR)
        )

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

            timeEventAdapter = TimeEventAdapter(requireActivity())
            timeEventRcView.adapter = timeEventAdapter
            timeEventRcView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpDataBase() {
        val userUid = auth.currentUser?.uid ?: ""
        // Get personal task
        firebaseManager.getPersonalTask(userUid, {
            personalTaskAdapter.setTask(it, currentDate)
            Log.d("Personal affairs", "$it")
        }, {
            Log.d("Loading personal", "Error of loading data")
        })

        // Get work task
        firebaseManager.getWorkTask(userUid, {
            workTaskAdapter.setTask(it, currentDate)
            Log.d("Personal affairs", "$it")
        }, {
            Log.d("Loading work", "Error of loading data")
        })

        // Get time events
        firebaseManager.getTimeEvent(helper.getUid(), {
            timeEventAdapter.setTimeEvent(it, currentDate)
        }, {
            Log.d("Loading time event", "Error of loading data")
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
                helper.navigate(CreateTaskFragment(selectedDate, null, personalTask))
            }

            // Create work
            btnCreateWork.setOnClickListener {
                helper.navigate(CreateTaskFragment(selectedDate, currentDate, workTask))
            }

            // Opening personal task for editing
            personalTaskAdapter.setOnClickListener(object : TaskAdapter.OnClickListener {
                override fun onClick(task: Task) {
                    helper.navigate(CreateTaskFragment(selectedDate, task.uniqueId, personalTask))
                }
            })

            // Opening work task for editing
            workTaskAdapter.setOnClickListener(object : TaskAdapter.OnClickListener {
                override fun onClick(task: Task) {
                    helper.navigate(CreateTaskFragment(selectedDate, task.uniqueId, workTask))
                }
            })

            // Create time event
            btnCreateTimeEvent.setOnClickListener {
                // Check permission for notifications
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val permissionCheck = PermissionCheckFragment()
                    permissionCheck.show(
                        requireActivity().supportFragmentManager, "GrantPermissionFragment"
                    )
                } else {
                    val createTimeEvent = CreateTimeEventFragment(selectedDate)
                    createTimeEvent.show(
                        requireActivity().supportFragmentManager, "CreateTimeEventFragment"
                    )
                }
            }

            // Create habit
//            btnCreateHabit.setOnClickListener {

 //           }
        }
    }

//    companion object {
//        fun newInstance() = DateNoteFragment()
//    }
}