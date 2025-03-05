import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData

@Composable
fun TotalsBarChart(totalProperties: Int, totalListedProperties: Int, totalSoldProperties: Int) {
    // Prepare chart data
    val barChartData = listOf(
        BarData(
            label = "Total",
            point = Point(totalProperties.toFloat(), 0f), // X and Y coordinates
            color = Color(0xFF4CAF50) // Green color for bars
        ),
        BarData(
            label = "Listed",
            point = Point(totalListedProperties.toFloat(), 1f), // X and Y coordinates
            color = Color(0xFF2196F3) // Blue color for bars
        ),
        BarData(
            label = "Sold",
            point = Point(totalSoldProperties.toFloat(), 2f), // X and Y coordinates
            color = Color(0xFFF44336) // Red color for bars
        )
    )

    // Initialize X-Axis data
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp) // Step size between bars
        .steps(barChartData.size - 1) // Number of steps
        .bottomPadding(40.dp) // Padding at the bottom
        .axisLabelAngle(20f) // Angle of X-axis labels
        .labelData { index -> barChartData[index].label } // Labels for X-axis
        .build()

    // Initialize Y-Axis data
    val yAxisData = AxisData.Builder()
        .steps(5) // Number of steps on Y-axis
        .labelAndAxisLinePadding(20.dp) // Padding for labels and axis lines
        .axisOffset(20.dp) // Offset for the Y-axis
        .labelData { index -> (index * (100 / 5)).toString() } // Labels for Y-axis
        .build()

    // Create BarChartData
    val barChartConfig = BarChartData(
        chartData = barChartData, // Bar data
        xAxisData = xAxisData, // X-axis configuration
        yAxisData = yAxisData, // Y-axis configuration
        //   paddingBetweenBars = 20.dp, // Padding between bars
        //   barWidth = 25.dp // Width of each bar
    )

    // Display the bar chart
    BarChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp) // Height of the chart
            .padding(16.dp), // Padding around the chart
        barChartData = barChartConfig // Bar chart configuration
    )
}