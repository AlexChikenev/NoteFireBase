package com.example.notefirebase.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Chart
import com.example.notefirebase.firebasemodel.Income
import com.example.notefirebase.firebasemodel.IncomeAndOutcome
import com.example.notefirebase.fragments.finance.DeleteItemFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import java.text.DecimalFormat

class ChartAdapter(private val context: Context) :
    RecyclerView.Adapter<ChartAdapter.ChartViewHolder>() {

    private var chartList = emptyList<Chart>()
    //private var chartEntries: List<List<PieEntry>> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_chart_item, parent, false)
        return ChartViewHolder(itemView)
    }

    inner class ChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pieChart: PieChart = itemView.findViewById(R.id.pieChart)
        val textDate: TextView = itemView.findViewById(R.id.textDate)
        val textIncomePercent: TextView = itemView.findViewById(R.id.textIncomePercent)
        val textOutcomePercent: TextView = itemView.findViewById(R.id.textOutcomePercent)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        with(holder) {
            if (chartList.isEmpty()){
                val entries = listOf(
                    PieEntry(0.0f, ""),
                    PieEntry(0.0f, "")
                )
                val firebaseManager = FirebaseManager()
                val (year, month) =  firebaseManager.getCurrentYearAndMonth()
                val currentDate = "$year-$month"
                textDate.text = currentDate
                textIncomePercent.text = "0%"
                textOutcomePercent.text = "0%"
                setChartAppearance(entries, holder)
            } else{
                val currentChart = chartList[position]
                val decimalFormat = DecimalFormat("0.#")
                val date = currentChart.incomeDate

                if (currentChart.outcomePercent != null && currentChart.incomePercent != null) {
                    val entries = listOf(
                        PieEntry(currentChart.incomePercent.toFloat(), ""),
                        PieEntry(currentChart.outcomePercent.toFloat(), "")
                    )
                    textDate.text = date
                    textIncomePercent.text =
                        decimalFormat.format(currentChart.incomePercent.toDouble()) + "%"
                    textOutcomePercent.text =
                        decimalFormat.format(currentChart.outcomePercent.toDouble()) + "%"
                    setChartAppearance(entries, holder)
                } else if (currentChart.incomePercent == null && currentChart.outcomePercent != null) {
                    val entries = listOf(
                        PieEntry(currentChart.outcomePercent.toFloat(), "")
                    )
                    textDate.text = date
                    textIncomePercent.text = "0%"
                    textOutcomePercent.text =
                        decimalFormat.format(currentChart.outcomePercent.toDouble()) + "%"
                    setChartAppearance(entries, holder)
                } else if (currentChart.incomePercent != null && currentChart.outcomePercent == null) {
                    val entries = listOf(
                        PieEntry(currentChart.incomePercent.toFloat(), "")
                    )
                    textDate.text = date
                    textIncomePercent.text =
                        decimalFormat.format(currentChart.incomePercent.toDouble()) + "%"
                    textOutcomePercent.text = "0%"
                    setChartAppearance(entries, holder)
                } else {
                    val entries = listOf(
                        PieEntry(0.0f, "Нет данных")
                    )
                    setChartAppearance(entries, holder)
                }
            }
        }
    }

    private fun setChartAppearance(entries: List<PieEntry>, holder: ChartViewHolder) {
        with(holder) {
            // Set data of one pie
            val dataSet = PieDataSet(entries, "")
            // Set data in pie
            val pieData = PieData(dataSet)
            // Set data in chart
            pieChart.data = pieData
            // Set pie background color
            dataSet.colors = getElementColors()
            // Set font appearance
            pieData.setValueTextSize(12f)
            pieData.setValueTypeface(getTypeface())

            // Set % symbol
            val valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("%.1f%%", value)
                }
            }
            pieData.setValueFormatter(valueFormatter)

            // Animation
            pieChart.animateY(1200, Easing.EaseInOutQuad)

            pieChart.setDrawEntryLabels(false)
            pieChart.isDrawHoleEnabled = false
            pieChart.legend.isEnabled = false
            pieChart.description.isEnabled = false
            // Draw chart
            pieChart.invalidate()
        }
    }

    fun setChart(
        incomesMap: Map<String, Double>,
        outcomesMap: Map<String, Double> /*chartEntries: List<List<PieEntry>>*/
    ) {
        val combinedList = mutableListOf<Chart>()
        val maxIndex = StrictMath.max(incomesMap.size, outcomesMap.size)

        for (i in 0 until maxIndex) {
            val incomeDate = incomesMap.keys.elementAtOrNull(i)
            val outcomeDate = outcomesMap.keys.elementAtOrNull(i)

            val incomeAmount = incomesMap[incomeDate] ?: 0.0
            val outcomeAmount = outcomesMap[outcomeDate] ?: 0.0

            val total = incomeAmount + outcomeAmount
            val incomePercent = (incomeAmount / total) * 100
            val outcomePercent = (outcomeAmount / total) * 100

            val data = if (incomeDate != null && outcomeDate != null) {
                Chart(incomePercent, incomeDate, outcomePercent, outcomeDate)
            } else if (incomeDate != null) {
                Chart(incomePercent, incomeDate, null, null)
            } else if (outcomeDate != null) {
                Chart(null, null, outcomePercent, outcomeDate)
            } else {
                Chart(null, null, null, null)
            }
            combinedList.add(data)
        }
        combinedList.reverse()
        chartList = combinedList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return if (chartList.isEmpty()) {
            1
        } else {
            return chartList.size
        }
    }


    // Get colors for elements
    private fun getElementColors(): List<Int> {
        // Income
        val incomeColor = ContextCompat.getColor(context, R.color.income_chart_color)
        // Outcome
        val outcomeColor = ContextCompat.getColor(context, R.color.outcome_chart_color)
        // Accumulate
        val accumulateColor = ContextCompat.getColor(context, R.color.accumulate_chart_color)

        return listOf(incomeColor, outcomeColor, accumulateColor)
    }

    // Set font typeface
    private fun getTypeface(): Typeface? {
        return ResourcesCompat.getFont(context, R.font.helvetica_bold)
    }
}
