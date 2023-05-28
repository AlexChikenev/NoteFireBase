package com.example.notefirebase.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.IncomeAndOutcome
import java.lang.StrictMath.max

class IncomeAndOutcomeAdapter :
    RecyclerView.Adapter<IncomeAndOutcomeAdapter.IncomeAndOutcomeViewHolder>() {

    private var incomeAndOutcomeList = emptyList<IncomeAndOutcome>()
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeAndOutcomeViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.income_outcome_item, parent, false)
        return IncomeAndOutcomeViewHolder(itemView)
    }

    inner class IncomeAndOutcomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateText)
        val amountTextView: TextView = itemView.findViewById(R.id.incomeOutcomeText)
    }

    override fun onBindViewHolder(holder: IncomeAndOutcomeViewHolder, position: Int) {
        val amountText = if (incomeAndOutcomeList.isEmpty()) {
            "Нет данных"
        } else {
            val currentIncomeAndOutcome = incomeAndOutcomeList[position]
            val incomeAmount = currentIncomeAndOutcome.IncomeAmount
            val outcomeAmount = currentIncomeAndOutcome.OutcomeAmount
            if (currentIncomeAndOutcome.IncomeDate == null) {
                val date = currentIncomeAndOutcome.OutcomeDate
                holder.dateTextView.text = date
            } else {
                val date = currentIncomeAndOutcome.IncomeDate
                holder.dateTextView.text = date
            }

            if (incomeAmount != null && outcomeAmount != null) {
                "Доходы: $incomeAmount | Расходы: $outcomeAmount"
            } else if (incomeAmount != null) {
                "Доходы: $incomeAmount"
            } else if (outcomeAmount != null) {
                "Расходы: $outcomeAmount"
            } else {
                "Нет данных"
            }
        }
        holder.amountTextView.text = amountText
    }

    override fun getItemCount(): Int {
        return if (incomeAndOutcomeList.isEmpty()) {
            1
        } else {
            return incomeAndOutcomeList.size
        }
    }

    fun setIncomes(incomesMap: Map<String, Double>, outcomesMap: Map<String, Double>) {
        val combinedList = mutableListOf<IncomeAndOutcome>()
        val maxIndex = max(incomesMap.size, outcomesMap.size)

        for (i in 0 until maxIndex) {
            val incomeDate = incomesMap.keys.elementAtOrNull(i)
            val outcomeDate = outcomesMap.keys.elementAtOrNull(i)

            val incomeAmount = incomesMap[incomeDate] ?: 0.0
            val outcomeAmount = outcomesMap[outcomeDate] ?: 0.0

            val incomeAndOutcome = if (incomeDate != null && outcomeDate != null) {
                IncomeAndOutcome(incomeAmount, incomeDate, outcomeAmount, outcomeDate)
            } else if (incomeDate != null) {
                IncomeAndOutcome(incomeAmount, incomeDate, null, null)
            } else if (outcomeDate != null) {
                IncomeAndOutcome(null, null, outcomeAmount, outcomeDate)
            } else {
                IncomeAndOutcome(null, null, null, null)
            }
            combinedList.add(incomeAndOutcome)
        }

        incomeAndOutcomeList = combinedList
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(incomesAndOutcomes: IncomeAndOutcome)
    }
}
