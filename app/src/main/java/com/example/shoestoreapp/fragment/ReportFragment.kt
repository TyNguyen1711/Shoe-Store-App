package com.example.shoestoreapp.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import android.widget.TextView
import com.example.shoestoreapp.R
import java.text.NumberFormat
import java.util.Locale

class ReportFragment : Fragment() {
    private lateinit var orderLineChart: LineChart
    private lateinit var revenueBarChart: BarChart
    private lateinit var tvAverageRevenue: TextView
    private lateinit var tvAverageOrders: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        orderLineChart = view.findViewById(R.id.orderLineChart)
        revenueBarChart = view.findViewById(R.id.revenueBarChart)
        tvAverageRevenue = view.findViewById(R.id.tvAverageRevenue)
        tvAverageOrders = view.findViewById(R.id.tvAverageOrders)

        setupOrderChart()
        setupRevenueChart()
        updateAverageStats()
    }

    private fun updateAverageStats() {
        val revenue = listOf(150f, 200f, 180f, 250f, 220f, 280f, 230f, 260f, 290f, 270f, 300f, 320f)
        val averageRevenue = revenue.average()

        val orderCounts = listOf(65f, 89f, 76f, 92f, 87f, 94f, 78f, 85f, 95f, 88f, 91f, 99f)
        val averageOrders = orderCounts.average()

        val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        tvAverageRevenue.text = numberFormat.format(averageRevenue.toInt())
        tvAverageOrders.text = numberFormat.format(averageOrders.toInt())
    }

    private fun setupOrderChart() {
        val months = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val orderCounts = listOf(120, 145, 135, 160, 155, 170, 158, 165, 180, 175, 185, 190)

        val entries = orderCounts.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }

        val integerFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val dataSet = LineDataSet(entries, "Số đơn hàng").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = Color.parseColor("#2196F3")
            lineWidth = 2.5f

            setCircleColor(Color.parseColor("#2196F3"))
            circleRadius = 5f
            circleHoleRadius = 3f
            circleHoleColor = Color.WHITE

            setDrawFilled(true)
            fillAlpha = 50
            fillColor = Color.parseColor("#2196F3")

            setDrawValues(true)
            valueTextSize = 11f
            valueTextColor = Color.parseColor("#2196F3")
            valueFormatter = integerFormatter
        }

        orderLineChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            legend.textSize = 12f

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(months)
                granularity = 1f
                labelRotationAngle = 0f
                setDrawGridLines(false)
                textSize = 11f
            }

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 200f
                granularity = 50f
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E0E0E0")
                textSize = 11f
                valueFormatter = integerFormatter
            }

            axisRight.isEnabled = false

            setPadding(16, 16, 16, 16)

            animateXY(1500, 1500)

            invalidate()
        }
    }

    private fun setupRevenueChart() {
        val months = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val revenue = listOf(150f, 200f, 180f, 250f, 220f, 280f, 230f, 260f, 290f, 270f, 300f, 320f)

        val entries = revenue.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(entries, "Doanh thu (triệu đồng)")
        dataSet.apply {
            color = Color.GREEN
            valueTextSize = 10f
        }

        revenueBarChart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(months)
                granularity = 1f

            }

            axisRight.isEnabled = false

            axisLeft.apply {
                axisMinimum = 0f
                granularity = 50f
            }

            animateY(1500)
            invalidate()
        }
    }
}
