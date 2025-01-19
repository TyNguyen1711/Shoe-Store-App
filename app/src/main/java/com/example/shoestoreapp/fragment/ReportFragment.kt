//
//package com.example.shoestoreapp.fragment
//
//import android.graphics.Color
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//
//import com.github.mikephil.charting.charts.BarChart
//import com.github.mikephil.charting.data.*
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.formatter.ValueFormatter
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
//import android.widget.TextView
//import com.example.shoestoreapp.R
//import com.example.shoestoreapp.data.model.Order
//import com.example.shoestoreapp.data.repository.OrderRepository
//import java.text.NumberFormat
//import java.util.Locale
//import java.text.SimpleDateFormat
//import java.util.*
//import kotlinx.coroutines.launch
//
//class ReportFragment : Fragment() {
//    private lateinit var orderLineChart: BarChart
//    private lateinit var revenueBarChart: BarChart
//    private lateinit var tvAverageRevenue: TextView
//    private lateinit var tvAverageOrders: TextView
//
//    private lateinit var orderRepository: OrderRepository
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_report, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        orderLineChart = view.findViewById(R.id.orderLineChart)
//        revenueBarChart = view.findViewById(R.id.revenueBarChart)
//        tvAverageRevenue = view.findViewById(R.id.tvAverageRevenue)
//        tvAverageOrders = view.findViewById(R.id.tvAverageOrders)
//
//        orderRepository = OrderRepository()
//
//        loadOrderData()
//    }
//
//    private fun loadOrderData() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            val result = orderRepository.getOrders()
//            result.onSuccess { orders ->
//                val monthlyData = processOrderData(orders)
//                setupOrderChart(monthlyData)
//                setupRevenueChart(monthlyData)
//                updateAverageStats(monthlyData)
//            }.onFailure {
//            }
//        }
//    }
//
//    data class MonthlyStats(
//        val orderCount: Int = 0,
//        val revenue: Double = 0.0
//    )
//
//    private fun processOrderData(orders: List<Order>): Map<Int, MonthlyStats> {
//        val calendar = Calendar.getInstance()
//        val currentYear = calendar.get(Calendar.YEAR)
//        val currentMonth = calendar.get(Calendar.MONTH) + 1
//        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
//
//        val monthlyData = (1..currentMonth).associateWith { MonthlyStats() }.toMutableMap()
//
//        orders.forEach { order ->
//            try {
//                val orderDate = dateFormat.parse(order.orderTime)
//                calendar.time = orderDate
//
//                val orderYear = calendar.get(Calendar.YEAR)
//                val orderMonth = calendar.get(Calendar.MONTH) + 1
//
//                if (orderYear == currentYear && orderMonth <= currentMonth) {
//                    val currentStats = monthlyData[orderMonth] ?: MonthlyStats()
//                    monthlyData[orderMonth] = currentStats.copy(
//                        orderCount = currentStats.orderCount + 1,
//                        revenue = currentStats.revenue + order.totalPayment
//                    )
//                }
//            } catch (e: Exception) {
//            }
//        }
//
//        return monthlyData
//    }
//
//    private fun updateAverageStats(monthlyData: Map<Int, MonthlyStats>) {
//        val averageRevenue = monthlyData.values.map { it.revenue }.average()
//        val averageOrders = monthlyData.values.map { it.orderCount }.average()
//
//        val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
//        tvAverageRevenue.text = numberFormat.format(averageRevenue / 1000000)
//        tvAverageOrders.text = numberFormat.format(averageOrders)
//    }
//
//    private fun setupOrderChart(monthlyData: Map<Int, MonthlyStats>) {
////        val months = monthlyData.keys.map { it.toString() }.toTypedArray()
//        val months = monthlyData.keys.map { "Tháng $it" }.toTypedArray()
//
//        val orderCounts = monthlyData.values.map { it.orderCount }
//
//        val entries = orderCounts.mapIndexed { index, value ->
//            BarEntry(index.toFloat(), value.toFloat())
//        }
//
//        val integerFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return value.toInt().toString()
//            }
//        }
//
//        val dataSet = BarDataSet(entries, "Số đơn hàng").apply {
//            color = Color.parseColor("#2196F3")
//            valueTextSize = 11f
//            valueTextColor = Color.parseColor("#2196F3")
//            valueFormatter = integerFormatter
//        }
//
//        orderLineChart.apply {
//            data = BarData(dataSet)
//            description.isEnabled = false
//            legend.isEnabled = true
//            legend.textSize = 12f
//
//            xAxis.apply {
//                position = XAxis.XAxisPosition.BOTTOM
//                valueFormatter = IndexAxisValueFormatter(months)
//                granularity = 1f
//                labelRotationAngle = 0f
//                setDrawGridLines(false)
//                textSize = 11f
//            }
//
//            axisLeft.apply {
//                axisMinimum = 0f
//                axisMaximum = (orderCounts.maxOrNull()?.toFloat() ?: 200f) * 1.2f
//                granularity = 50f
//                setDrawGridLines(true)
//                gridColor = Color.parseColor("#E0E0E0")
//                textSize = 11f
//                valueFormatter = integerFormatter
//            }
//
//            axisRight.isEnabled = false
//            setPadding(16, 16, 16, 16)
//            animateY(1500)
//            invalidate()
//        }
//    }
//
//    private fun setupRevenueChart(monthlyData: Map<Int, MonthlyStats>) {
////        val months = monthlyData.keys.map { it.toString() }.toTypedArray()
//        val months = monthlyData.keys.map { "Tháng $it" }.toTypedArray()
//
//        val revenue = monthlyData.values.map { it.revenue.toFloat() / 1000000f }
//
//        val entries = revenue.mapIndexed { index, value ->
//            BarEntry(index.toFloat(), value)
//        }
//
//        val dataSet = BarDataSet(entries, "Doanh thu (triệu đồng)").apply {
//            color = Color.GREEN
//            valueTextSize = 10f
//            valueFormatter = object : ValueFormatter() {
//                override fun getFormattedValue(value: Float): String {
//                    return value.toInt().toString()
//                }
//            }
//        }
//
//        revenueBarChart.apply {
//            data = BarData(dataSet)
//            description.isEnabled = false
//            legend.isEnabled = true
//
//            xAxis.apply {
//                position = XAxis.XAxisPosition.BOTTOM
//                valueFormatter = IndexAxisValueFormatter(months)
//                granularity = 1f
//            }
//
//            axisRight.isEnabled = false
//
//            axisLeft.apply {
//                axisMinimum = 0f
//                axisMaximum = (revenue.maxOrNull() ?: 300f) * 1.2f
//                granularity = 50f
//            }
//
//            animateY(1500)
//            invalidate()
//        }
//    }
//}