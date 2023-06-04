package com.example.notefirebase.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Income
import com.example.notefirebase.fragments.finance.DeleteItemFragment
import com.example.notefirebase.utils.Helper

class IncomeAdapter(private val activity: FragmentActivity) :
    RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    private var incomes: List<Income> = emptyList()
    private lateinit var helper: Helper
    private var selectedPosition: Int = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.income_item, parent, false)
        helper = Helper(activity)
        return IncomeViewHolder(itemView)
    }

    inner class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.incomeName)
        val amountTextView: TextView = itemView.findViewById(R.id.incomeAmount)

        // Set long click listener
        init {
            itemView.setOnLongClickListener {
                helper.vibrateDevice()
                // Delete selected income
                val selectedIncome = incomes[adapterPosition]
                val deleteIncome = DeleteItemFragment(selectedIncome, null, 0)
                deleteIncome.show(activity.supportFragmentManager, "DeleteSelectedItem")
                true
            }
        }
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val currentIncome = incomes[position]
        holder.nameTextView.text = currentIncome.incomeName
        holder.amountTextView.text = currentIncome.incomeAmount.toString()

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(activity, R.color.black))
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

    }

    override fun getItemCount(): Int {
        return incomes.size
    }

    fun setIncomes(incomeList: List<Income>, targetDate: String) {
        incomes = incomeList.filter { it.incomeDate == targetDate }
        notifyDataSetChanged()
    }
}
