package com.example.notefirebase.fragments.finance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.adapters.IncomeAdapter
import com.example.notefirebase.adapters.OutcomeAdapter
import com.example.notefirebase.databinding.FragmentFinanceMainBinding
import com.example.notefirebase.firebasemodel.FirebaseIncomes
import com.example.notefirebase.firebasemodel.FirebaseOutcomes
import com.example.notefirebase.firebasemodel.FirebasePillows
import com.example.notefirebase.firebasemodel.Income
import com.example.notefirebase.firebasemodel.Outcome
import com.example.notefirebase.firebasemodel.Pillow
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FinanceMainFragment : Fragment() {
    private lateinit var fragmentBinding: FragmentFinanceMainBinding
    private lateinit var incomeAdapter: IncomeAdapter
    private lateinit var outcomeAdapter: OutcomeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var helper: Helper
    private lateinit var firebaseManager: FirebaseManager

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
        helper = Helper(requireActivity())
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseManager = FirebaseManager()
        setupDatabase()
        setupRecyclerView()
    }

    private fun setupDatabase() {
        // Get incomes
        val userUid = auth.currentUser?.uid ?: ""
        firebaseManager.getIncome(userUid, { incomeList ->
            incomeAdapter.setIncomes(incomeList)
        }, { totalIncomeAmount ->
            fragmentBinding.totalIncomeText.text = totalIncomeAmount.toString()
        }, {
            Log.d("Error", "Не удалось загрузить данные")
        })

        // Get outComes
        firebaseManager.getOutcome(userUid,
            { outcomeList ->
                outcomeAdapter.setOutcomes(outcomeList)
            }, { totalOutcomeAmount ->
                fragmentBinding.totalOutcomeText.text = totalOutcomeAmount.toString()
            }, {
                Log.d("Error", "Не удалось загрузить данные")
            })

        val pillowRef = databaseReference.child("Users").child(userUid).child("Pillow")
        pillowRef.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val pillow = dataSnapshot.getValue(FirebasePillows::class.java)
                    val pillowContent = pillow?.pillowAmount?.let { Pillow(it) }
                    if (pillowContent != null) {
                        fragmentBinding.showPillow.text =
                            pillowContent.pillowAmount.toString() + " " + "₽"
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Error", "Не удалось загрузить данные")
                }
            })
    }


    private fun setupRecyclerView() {
        with(fragmentBinding) {
            incomeAdapter = IncomeAdapter()
            fragmentBinding.incomeRcView.adapter = incomeAdapter
            fragmentBinding.incomeRcView.layoutManager = LinearLayoutManager(requireContext())

            outcomeAdapter = OutcomeAdapter()
            fragmentBinding.outcomeRcView.adapter = outcomeAdapter
            fragmentBinding.outcomeRcView.layoutManager = LinearLayoutManager(requireContext())
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

            btnInputOutcome.setOnClickListener {
                val createIncome = CreateOutcomeFragment()
                createIncome.show(
                    requireActivity().supportFragmentManager,
                    "CreateOutcomeFragment"
                )
            }

            btnCreatePillow.setOnClickListener {
                val createPillow = CreatePillowFragment()
                createPillow.show(
                    requireActivity().supportFragmentManager,
                    "CreatePillowFragment"
                )
            }

            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }

            btnToSettings.setOnClickListener {
                helper.navigate(MainSettingsFragment())
            }
        }
    }

    companion object {
        fun newInstance() = FinanceMainFragment()
    }
}