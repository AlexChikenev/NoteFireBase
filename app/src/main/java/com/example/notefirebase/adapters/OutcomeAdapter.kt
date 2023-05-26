package com.example.notefirebase.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Income
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.firebasemodel.Outcome
import com.example.notefirebase.firebasemodel.Project

class OutcomeAdapter : RecyclerView.Adapter<OutcomeAdapter.OutcomeViewHolder>() {

    private var outcomes: List<Outcome> = emptyList()
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutcomeViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.income_item, parent, false)
        return OutcomeViewHolder(itemView)
    }

    inner class OutcomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.incomeName)
        val amountTextView: TextView = itemView.findViewById(R.id.incomeAmount)
    }

    override fun onBindViewHolder(holder: OutcomeViewHolder, position: Int) {
        val currentOutcome = outcomes[position]
        holder.nameTextView.text = currentOutcome.outcomeName
        holder.amountTextView.text = currentOutcome.outcomeAmount.toString()

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(currentOutcome)
        }
    }

    override fun getItemCount(): Int {
        return outcomes.size
    }

    fun setOutcomes(outcomeList: List<Outcome>) {
        outcomes = outcomeList
        notifyDataSetChanged()
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(outcome: Outcome)
    }
}
