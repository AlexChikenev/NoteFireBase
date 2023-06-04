package com.example.notefirebase.fragments.finance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.adapters.ChartAdapter
import com.example.notefirebase.databinding.FragmentChartsBinding
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth


class ChartsFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentChartsBinding
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var auth: FirebaseAuth
    private lateinit var chartAdapter: ChartAdapter
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentChartsBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseManager = FirebaseManager()
        auth = FirebaseAuth.getInstance()
        helper = Helper(requireActivity())

        chartAdapter = ChartAdapter(requireContext())
        fragmentBinding.chartRcView.adapter = chartAdapter
        fragmentBinding.chartRcView.layoutManager = LinearLayoutManager(requireContext())
        setUpDatabase()
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Go back
            btnGoBack.setOnClickListener {
                helper.navigate(FinanceMainFragment())
            }
            // To settings
            btnToSettings.setOnClickListener {
                helper.navigate(MainSettingsFragment())
            }
            // To main
            btnToMain.setOnClickListener {
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
                chartAdapter.setChart(monthlyIncomes, monthlyOutcomes)
            }, {})
        }, {
            Log.d("Error", "Не удалось загрузить данные")
        })
    }
}