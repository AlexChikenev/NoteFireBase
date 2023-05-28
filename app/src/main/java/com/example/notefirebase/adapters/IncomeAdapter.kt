package com.example.notefirebase.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Income

class IncomeAdapter : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    private var incomes: List<Income> = emptyList()
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.income_item, parent, false)
        return IncomeViewHolder(itemView)
    }

    inner class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.incomeName)
        val amountTextView: TextView = itemView.findViewById(R.id.incomeAmount)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val currentIncome = incomes[position]
        holder.nameTextView.text = currentIncome.incomeName
        holder.amountTextView.text = currentIncome.incomeAmount.toString()

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(currentIncome)
        }
    }

    override fun getItemCount(): Int {
        return incomes.size
    }

    fun setIncomes(incomeList: List<Income>, targetDate: String) {
        incomes = incomeList.filter { it.incomeDate == targetDate }
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(income: Income)
    }
}
