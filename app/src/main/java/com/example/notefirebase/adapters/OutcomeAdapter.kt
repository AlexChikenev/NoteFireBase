package com.example.notefirebase.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Outcome
import com.example.notefirebase.fragments.finance.DeleteItemFragment
import com.example.notefirebase.utils.Helper

class OutcomeAdapter(private val activity: FragmentActivity) :
    RecyclerView.Adapter<OutcomeAdapter.OutcomeViewHolder>() {

    private var outcomes: List<Outcome> = emptyList()
    private lateinit var helper: Helper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutcomeViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.income_item, parent, false)
        helper = Helper(activity)
        return OutcomeViewHolder(itemView)
    }

    inner class OutcomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.incomeName)
        val amountTextView: TextView = itemView.findViewById(R.id.incomeAmount)

        // Set long click listener
        init {
            itemView.setOnLongClickListener {
                helper.vibrateDevice()
                // Delete selected outcome
                val selectedOutcome = outcomes[adapterPosition]
                val deleteOutcome = DeleteItemFragment(null, selectedOutcome, 1)
                deleteOutcome.show(activity.supportFragmentManager, "DeleteSelectedItem")

                true
            }
        }
    }

    override fun onBindViewHolder(holder: OutcomeViewHolder, position: Int) {
        val currentOutcome = outcomes[position]
        holder.nameTextView.text = currentOutcome.outcomeName
        holder.amountTextView.text = currentOutcome.outcomeAmount.toString()


    }

    override fun getItemCount(): Int {
        return outcomes.size
    }

    fun setOutcomes(outcomeList: List<Outcome>, targetDate: String) {
        outcomes = outcomeList.filter { it.outcomeDate == targetDate }
        notifyDataSetChanged()
    }
}
