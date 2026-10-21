package hung.lct.chitieu.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import hung.lct.chitieu.core.designsystem.R

private val RobotoFlex = FontFamily(
    Font(
        resId = R.font.roboto_flex,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Normal.weight),
        ),
    ),
    Font(
        resId = R.font.roboto_flex,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Medium.weight),
        ),
    ),
    Font(
        resId = R.font.roboto_flex,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.SemiBold.weight),
        ),
    ),
    Font(
        resId = R.font.roboto_flex,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Bold.weight),
        ),
    ),
)

private val defaultTypography = Typography()

val ChiTieuTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = RobotoFlex),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = RobotoFlex),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = RobotoFlex),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = RobotoFlex),
    headlineMedium = defaultTypography.headlineMedium.copy(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Bold,
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Bold,
    ),
    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.SemiBold,
    ),
    titleMedium = defaultTypography.titleMedium.copy(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.SemiBold,
    ),
    titleSmall = defaultTypography.titleSmall.copy(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Medium,
    ),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = RobotoFlex),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = RobotoFlex),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = RobotoFlex),
    labelLarge = defaultTypography.labelLarge.copy(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.SemiBold,
    ),
    labelMedium = defaultTypography.labelMedium.copy(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.SemiBold,
    ),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = RobotoFlex),
)
