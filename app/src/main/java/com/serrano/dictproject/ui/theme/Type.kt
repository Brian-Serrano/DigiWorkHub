package com.serrano.dictproject.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp
import com.serrano.dictproject.R

@OptIn(ExperimentalTextApi::class)
val robotoSlab = FontFamily(
    Font(
        R.font.robotoslab_variablefont_wght,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400)
        )
    )
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = robotoSlab,
        fontSize = 11.sp
    )
)