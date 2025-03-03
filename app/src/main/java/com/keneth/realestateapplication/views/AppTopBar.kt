package com.keneth.realestateapplication.views

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.keneth.realestateapplication.R

@Composable
fun AppTopBar(title: String, onMenuClick: () -> Unit) {

    val toBarFontFamily = FontFamily(
        Font(R.font.chalk_talk, weight = FontWeight.Normal),
        Font(R.font.seagram_tfb, weight = FontWeight.Bold)
    )
    TopAppBar(
        title = { Text(text = title,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = toBarFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            ) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                if (title.equals("dashboard", ignoreCase = true)) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                } else {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}
