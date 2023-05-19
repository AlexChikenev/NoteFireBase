package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notefirebase.R
import com.example.notefirebase.adapters.DirectoryAdapter
import com.example.notefirebase.databinding.FragmentDirectoryBinding
import com.example.notefirebase.firebasemodel.Directory
import com.example.notefirebase.firebasemodel.FirebaseDirectory
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DirectoryFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentDirectoryBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: DirectoryAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentDirectoryBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        //val directoryId: String
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        setupDatabase()
        setupClickListeners()
    }

    // Installing the adapter for RecyclerView
    private fun setupRecyclerView() {
        adapter = DirectoryAdapter()
        fragmentBinding.rcDirectory.adapter = adapter
        fragmentBinding.rcDirectory.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    // Loading data from the database
    private fun setupDatabase() {
        val userUid = auth.currentUser?.uid ?: ""
//        val email = FirebaseAuth.getInstance().currentUser?.email
//        val parts = email?.split("@")
//        val userEmail = parts!![0]
        val directoryRef = databaseReference.child("Users").child(userUid).child("Directory")
        directoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val directoryList = mutableListOf<Directory>()
                for (childSnapshot in dataSnapshot.children){
                    val directory = childSnapshot.getValue(FirebaseDirectory::class.java)
                    if (directory != null){
                        val dir = Directory(directory.directoryId, directory.name)
                        directoryList.add(dir)
                    }
                }
                adapter.setDirectories(directoryList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error", "Не удалось загрузить данные")
            }
        })
    }

    // Set up Click Listeners
    private fun setupClickListeners() {
        fragmentBinding.btnCreateDirectory.setOnClickListener {
            val createDirectory = CreateDirectoryFragment()
            createDirectory.show(
                requireActivity().supportFragmentManager,
                "CreateDirectoryFragment"
            )
        }

        adapter.setOnClickListener(object : DirectoryAdapter.OnClickListener {
            override fun onClick(directory: Directory) {
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragmentHolder, OpenDirectoryFragment(directory.id, directory.name))
                    ?.commit()
            }
        })

        fragmentBinding.btnToMain.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, MainFragment())
                ?.commit()
        }

        fragmentBinding.btnToSettings.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, MainSettingsFragment())
                ?.commit()
        }
    }

    companion object {
        fun newInstance() = DirectoryFragment()
    }
}