package com.example.notefirebase.fragments.finance

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.R
import com.example.notefirebase.adapters.IncomeAdapter
import com.example.notefirebase.databinding.FragmentFinanceMainBinding
import com.example.notefirebase.firebasemodel.FirebaseDirectory
import com.example.notefirebase.firebasemodel.FirebaseIncomes
import com.example.notefirebase.firebasemodel.Income
import com.example.notefirebase.firebasemodel.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FinanceMainFragment : Fragment() {
    private lateinit var fragmentBinding: FragmentFinanceMainBinding
    private lateinit var incomeAdapter: IncomeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentFinanceMainBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListeners()
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        setupDatabase()
        setupRecyclerView()
    }

    private fun setupDatabase() {
        // Get incomes
        val userUid = auth.currentUser?.uid ?: ""
        val incomeRef = databaseReference.child("Users").child(userUid).child("Incomes")
        incomeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val incomeList = mutableListOf<Income>()
                for (childSnapshot in dataSnapshot.children) {
                    val income = childSnapshot.getValue(FirebaseIncomes::class.java)
                    if (income != null) {
                        val inc = Income(income.incomeName, income.incomeAmount!!)
                        incomeList.add(inc)
                    }
                }
                incomeAdapter.setIncomes(incomeList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error", "Не удалось загрузить данные")
            }
        })
    }


    private fun setupRecyclerView() {
        with(fragmentBinding) {
            incomeAdapter = IncomeAdapter()
            fragmentBinding.incomeRcView.adapter = incomeAdapter
            fragmentBinding.incomeRcView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            btnInputIncome.setOnClickListener {
                val createIncome = CreateIncomeFragment()
                createIncome.show(
                    requireActivity().supportFragmentManager,
                    "CreateIncomeFragment"
                )
            }
        }
    }

    companion object {
        fun newInstance() = FinanceMainFragment()
    }
}