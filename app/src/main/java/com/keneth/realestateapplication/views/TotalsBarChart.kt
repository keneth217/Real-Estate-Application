import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData

@Composable
fun TotalsPieChart(
    totalProperties: Int,
    totalListedProperties: Int,
    totalSoldProperties: Int,
    context: Context
) {
    // Prepare chart data
    val slices = listOf(
        PieChartData.Slice(
            label = "Total",
            value = totalProperties.toFloat(), // Value for the slice
            color = Color(0xFF4CAF50) // Green color for the slice
        ),
        PieChartData.Slice(
            label = "Listed",
            value = totalListedProperties.toFloat(), // Value for the slice
            color = Color(0xFF2196F3) // Blue color for the slice
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
        backgroundColor = Color.White, // Background color of the chart
        strokeWidth = 120f, // Width of the slices
        labelColor = Color.Black, // Color of the labels
        isClickOnSliceEnabled = true, // Enable click on slices
        labelFontSize = 16.sp, // Font size for labels
        labelType = PieChartConfig.LabelType.PERCENTAGE, // Show percentage labels
        isSumVisible = true, // Show the sum of all slices
        activeSliceAlpha = 0.9f, // Alpha for active slices
        isEllipsizeEnabled = true, // Enable ellipsize for long labels
        sliceLabelEllipsizeAt = TextUtils.TruncateAt.MIDDLE, // Ellipsize at the middle
        sliceLabelTypeface = Typeface.defaultFromStyle(Typeface.ITALIC), // Italic labels
        showSliceLabels = true // Show slice labels
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(8.dp)
    ) {
        // Display legends
        Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData, 3))

        // Display the pie chart
        PieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            pieChartData = pieChartData, // Pie chart data
            pieChartConfig = pieChartConfig // Pie chart configuration
        ) { slice ->
            // Show a toast when a slice is clicked
            Toast.makeText(context, slice.label, Toast.LENGTH_SHORT).show()
        }
    }
}