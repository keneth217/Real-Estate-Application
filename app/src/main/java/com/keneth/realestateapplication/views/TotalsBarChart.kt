import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import co.yml.charts.ui.piechart.models.PieChartData
import co.yml.charts.ui.piechart.models.PieChartConfig

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart

@Composable
fun TotalsPieChart(totalProperties: Int, totalListedProperties: Int, totalSoldProperties: Int) {
    // Prepare chart data
    val slices = listOf(
        PieChartData.Slice(
            label = "Total",
            value = totalProperties.toFloat(), // Value for the slice
            color = Color(0xFFC66962) // Green color for the slice
        ),
        PieChartData.Slice(
            label = "Listed",
            value = totalListedProperties.toFloat(), // Value for the slice
            color = Color(0xFFCC5BEC) // Blue color for the slice
        ),
        PieChartData.Slice(
            label = "Sold",
            value = totalSoldProperties.toFloat(), // Value for the slice
            color = Color(0xFFF44336) // Red color for the slice
        )
    )

    // Create PieChartData
    val pieChartData = PieChartData(
        slices = slices, // Slice data
        plotType = PlotType.Pie // Type of chart (Pie or Donut)
    )

    // Create PieChartConfig
    val pieChartConfig = PieChartConfig(
        isAnimationEnable = true, // Enable animation
        chartPadding = 20, // Padding around the chart
        labelVisible = true, // Show labels on slices
        backgroundColor = Color.LightGray, // Background color of the chart
        strokeWidth = 100f, // Width of the slices
        labelColor = Color.Black ,// Color of the labels
        isClickOnSliceEnabled = true,
        labelFontSize = 24.sp,
        labelType = PieChartConfig.LabelType.PERCENTAGE,
        isSumVisible = true,
    )

    // Display the pie chart
    PieChart(


        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Height of the chart
            .padding(10.dp), // Padding around the chart
        pieChartData = pieChartData, // Pie chart data
        pieChartConfig = pieChartConfig // Pie chart configuration
    )
}