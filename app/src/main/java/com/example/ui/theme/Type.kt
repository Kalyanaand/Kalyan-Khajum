package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppFontFamilyOption
import com.example.data.model.FontSizeScale

// Local Google Fonts installed in res/font
val RobotoMonoFontFamily = FontFamily(
    Font(R.font.roboto_mono, FontWeight.Normal)
)

val MontserratFontFamily = FontFamily(
    Font(R.font.montserrat, FontWeight.Normal)
)

val OutfitFontFamily = FontFamily(
    Font(R.font.outfit, FontWeight.Normal)
)

fun getFontFamily(option: AppFontFamilyOption): FontFamily {
    return when (option) {
        AppFontFamilyOption.MONTSERRAT -> MontserratFontFamily
        AppFontFamilyOption.OUTFIT -> OutfitFontFamily
        AppFontFamilyOption.ROBOTO_MONO -> RobotoMonoFontFamily
        AppFontFamilyOption.SYSTEM_DEFAULT -> FontFamily.Default
    }
}

fun getScaledTypography(fontOption: AppFontFamilyOption, scale: FontSizeScale): Typography {
    val family = getFontFamily(fontOption)
    val factor = scale.scaleFactor

    return Typography(
        displayLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = (57 * factor).sp,
            lineHeight = (64 * factor).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = (32 * factor).sp,
            lineHeight = (40 * factor).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = (28 * factor).sp,
            lineHeight = (36 * factor).sp
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = (22 * factor).sp,
            lineHeight = (28 * factor).sp
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = (16 * factor).sp,
            lineHeight = (24 * factor).sp
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * factor).sp,
            lineHeight = (24 * factor).sp
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * factor).sp,
            lineHeight = (20 * factor).sp
        ),
        bodySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * factor).sp,
            lineHeight = (16 * factor).sp
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * factor).sp,
            lineHeight = (20 * factor).sp
        ),
        labelSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (11 * factor).sp,
            lineHeight = (16 * factor).sp
        )
    )
}

val Typography = getScaledTypography(AppFontFamilyOption.SYSTEM_DEFAULT, FontSizeScale.DEFAULT)
