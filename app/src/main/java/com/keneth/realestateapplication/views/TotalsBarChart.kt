package com.keneth.realestateapplication.views

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
    val slices = listOf(
        PieChartData.Slice(
            label = "Total",
            value = totalProperties.toFloat(),
            color = Color(0xFF15EE1D)
        ),
        PieChartData.Slice(
            label = "Listed",
            value = totalListedProperties.toFloat(),
            color = Color(0xFF2196F3)
        ),
        PieChartData.Slice(
            label = "Sold",
            value = totalSoldProperties.toFloat(),
            color = Color(0xFFF44336)
        )
    )

    val pieChartData = PieChartData(
        slices = slices,
        plotType = PlotType.Pie
    )

    val pieChartConfig = PieChartConfig(
        isAnimationEnable = true,
        chartPadding = 10,
        labelVisible = true,
       // backgroundColor = Color.White,
        strokeWidth = 20f,
        labelColor = Color.Black,
        isClickOnSliceEnabled = true,
        labelFontSize = 12.sp,
        labelType = PieChartConfig.LabelType.PERCENTAGE,
        isSumVisible = true,
        activeSliceAlpha = 0.5f,
        isEllipsizeEnabled = true,
        sliceLabelEllipsizeAt = TextUtils.TruncateAt.MIDDLE,
        sliceLabelTypeface = Typeface.defaultFromStyle(Typeface.ITALIC),
        showSliceLabels = true
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(2.dp)
    ) {
        Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData, 3))

        PieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            pieChartData = pieChartData,
            pieChartConfig = pieChartConfig
        ) { slice ->
            Toast.makeText(context, slice.label, Toast.LENGTH_SHORT).show()
        }
    }
}