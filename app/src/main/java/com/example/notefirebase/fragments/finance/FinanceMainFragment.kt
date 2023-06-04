package com.example.notefirebase.fragments.finance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.R
import com.example.notefirebase.adapters.IncomeAdapter
import com.example.notefirebase.adapters.OutcomeAdapter
import com.example.notefirebase.databinding.FragmentFinanceMainBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FinanceMainFragment : Fragment() {
    private lateinit var fragmentBinding: FragmentFinanceMainBinding
    private lateinit var incomeAdapter: IncomeAdapter
    private lateinit var outcomeAdapter: OutcomeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var helper: Helper
    private lateinit var firebaseManager: FirebaseManager
    private var saved: Double = 0.0
    private var aim: Double = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentFinanceMainBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper = Helper(requireActivity())
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseManager = FirebaseManager()
        setUpClickListeners()
        setupDatabase()
        setupRecyclerView()
    }


    private fun setupDatabase() {
        val userUid = auth.currentUser?.uid ?: ""
        val (year, month) = firebaseManager.getCurrentYearAndMonth()
        val formattedDate = "$year-$month"

        // Get incomes
        firebaseManager.getIncome(userUid, { incomeList ->
            incomeAdapter.setIncomes(incomeList, formattedDate)
        }, {
            Log.d("Error", "Не удалось загрузить данные")
        })

        // Get outcomes
        firebaseManager.getOutcome(userUid, { outcomeList ->
            outcomeAdapter.setOutcomes(outcomeList, formattedDate)
        }, {
            Log.d("Error", "Не удалось загрузить данные")
        })

        // Get incomes from database
        firebaseManager.getIncome(userUid, { incomes ->
            // Get outcomes from database
            firebaseManager.getOutcome(userUid, { outcomes ->
                // Calculate incomes per month
                val incomesMap = firebaseManager.calculateMonthlyIncomes(incomes)
                // Calculate outcomes per month
                val outcomesMap = firebaseManager.calculateMonthlyOutcomes(outcomes)

                // Get total income amount per month
                val incomeAmount = incomesMap[formattedDate] ?: 0.0
                fragmentBinding.totalIncomeText.text = incomeAmount.toString()

                // Get total outcome amount per month
                val outcomeAmount = outcomesMap[formattedDate] ?: 0.0
                fragmentBinding.totalOutcomeText.text = outcomeAmount.toString()

                calculateSaved(incomeAmount, outcomeAmount, userUid)

                // Get aim
                firebaseManager.getAim(userUid, { aimAmount ->
                    saved = incomeAmount - outcomeAmount
                    val remain = aimAmount - saved
                    if (remain < 0.0) {
                        fragmentBinding.showAim.text = "Вы накопили"
                    } else {
                        fragmentBinding.showAim.text = remain.toString()
                    }

                }, { zero -> fragmentBinding.showAim.text = zero.toString() }, {
                    Log.d("Error", "Не удалось загрузить данные")
                })
                fragmentBinding.showSave.text = saved.toString()

            }, {})
        }, {
            Log.d("Error", "Не удалось загрузить данные")
        })
    }

    // Calculate saved money
    private fun calculateSaved(income: Double, outcome: Double, userUid: String) {
        saved = income - outcome
    }

    private fun setupRecyclerView() {
        with(fragmentBinding) {
            incomeAdapter = IncomeAdapter(requireActivity())
            incomeRcView.adapter = incomeAdapter
            incomeRcView.layoutManager = LinearLayoutManager(requireContext())

            outcomeAdapter = OutcomeAdapter(requireActivity())
            outcomeRcView.adapter = outcomeAdapter
            outcomeRcView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Create income
            btnCreateIncome.setOnClickListener {
                val createIncome = CreateIncomeFragment()
                createIncome.show(
                    requireActivity().supportFragmentManager, "CreateIncomeFragment"
                )
            }

            // Create outcome
            btnCreateOutcome.setOnClickListener {
                val createIncome = CreateOutcomeFragment()
                createIncome.show(
                    requireActivity().supportFragmentManager, "CreateOutcomeFragment"
                )
            }

            // Create pillow
            btnCreateAim.setOnClickListener {
                val createAim = CreateAimFragment()
                createAim.show(
                    requireActivity().supportFragmentManager, "CreateAimFragment"
                )
            }

            // Go to main
            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }

            // Go to settings
            btnToSettings.setOnClickListener {
                helper.navigate(MainSettingsFragment())
            }

            // Go to month income and outcome
            btnCheckBefore.setOnClickListener {
                helper.navigate(MonthsFragment())
            }

            // Go to charts
            btnCharts.setOnClickListener {
                helper.navigate(ChartsFragment())
            }
        }
    }

    companion object {
        fun newInstance() = FinanceMainFragment()
    }
}