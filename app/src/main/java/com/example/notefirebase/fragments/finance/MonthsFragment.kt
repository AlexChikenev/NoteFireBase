package com.example.notefirebase.fragments.finance

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.adapters.IncomeAndOutcomeAdapter
import com.example.notefirebase.databinding.FragmentMonthsBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MonthsFragment : Fragment() {
    private lateinit var fragmentBinding: FragmentMonthsBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var incomeAdapter: IncomeAndOutcomeAdapter
    private lateinit var helper: Helper
    private lateinit var context: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentMonthsBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseManager = FirebaseManager()
        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        helper = Helper(requireActivity())
        setUpDatabase()
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Go back
            btnGoBack.setOnClickListener {
                helper.navigate(FinanceMainFragment())
            }

            // Go to main
            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }

            // Go to settings
            btnToSettings.setOnClickListener {
                helper.navigate(MainSettingsFragment())
            }
        }
    }

    private fun setUpDatabase() {
        val userUid = auth.currentUser?.uid ?: ""
        // Get incomes from database
        firebaseManager.getIncome(userUid, { incomes ->
            // Get outcomes from database
            firebaseManager.getOutcome(userUid, { outcomes ->
                // Calculate incomes per month
                val monthlyIncomes = firebaseManager.calculateMonthlyIncomes(incomes)
                // Calculate outcomes per month
                val monthlyOutcomes = firebaseManager.calculateMonthlyOutcomes(outcomes)

                incomeAdapter = IncomeAndOutcomeAdapter()
                fragmentBinding.rcTest.adapter = incomeAdapter
                fragmentBinding.rcTest.layoutManager = LinearLayoutManager(context)
                incomeAdapter.setIncomes(monthlyIncomes, monthlyOutcomes)
            }, {}, {})
        }, {}, {
            Log.d("Error", "Не удалось загрузить данные")
        })
    }

    companion object {
        fun newInstance() = MonthsFragment()
    }
}