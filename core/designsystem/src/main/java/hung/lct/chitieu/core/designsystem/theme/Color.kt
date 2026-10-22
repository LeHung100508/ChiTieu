package hung.lct.chitieu.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// --- Light Theme Colors ---
private val PrimaryLight = Color(0xFF1B6B4A)
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFA2F3C6)
private val OnPrimaryContainerLight = Color(0xFF002112)

private val SecondaryLight = Color(0xFF4C6355)
private val OnSecondaryLight = Color(0xFFFFFFFF)
private val SecondaryContainerLight = Color(0xFFCEE9D7)
private val OnSecondaryContainerLight = Color(0xFF092014)

private val TertiaryLight = Color(0xFF3C6472)
private val OnTertiaryLight = Color(0xFFFFFFFF)
private val TertiaryContainerLight = Color(0xFFC0E9FA)
private val OnTertiaryContainerLight = Color(0xFF001F28)

private val ErrorLight = Color(0xFFBA1A1A)
private val OnErrorLight = Color(0xFFFFFFFF)
private val ErrorContainerLight = Color(0xFFFFDAD6)
private val OnErrorContainerLight = Color(0xFF410002)

private val BackgroundLight = Color(0xFFFBFDF9)
private val OnBackgroundLight = Color(0xFF191C1A)
private val SurfaceLight = Color(0xFFFBFDF9)
private val OnSurfaceLight = Color(0xFF191C1A)
private val SurfaceVariantLight = Color(0xFFDBE5DE)
private val OnSurfaceVariantLight = Color(0xFF404944)
private val OutlineLight = Color(0xFF707973)
private val OutlineVariantLight = Color(0xFFBFC9C2)
private val ScrimLight = Color(0xFF000000)
private val InverseSurfaceLight = Color(0xFF2E312F)
private val InverseOnSurfaceLight = Color(0xFFEFF1ED)
private val InversePrimaryLight = Color(0xFF86D6AB)
private val SurfaceDimLight = Color(0xFFD8DBD6)
private val SurfaceBrightLight = Color(0xFFF8FAF6)
private val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
private val SurfaceContainerLowLight = Color(0xFFF2F4F0)
private val SurfaceContainerLight = Color(0xFFECEEEA)
private val SurfaceContainerHighLight = Color(0xFFE7E9E4)
private val SurfaceContainerHighestLight = Color(0xFFE1E3DF)

// --- Dark Theme Colors ---
private val PrimaryDark = Color(0xFF86D6AB)
private val OnPrimaryDark = Color(0xFF003822)
private val PrimaryContainerDark = Color(0xFF005134)
private val OnPrimaryContainerDark = Color(0xFFA2F3C6)

private val SecondaryDark = Color(0xFFB3CCBC)
private val OnSecondaryDark = Color(0xFF1F3528)
private val SecondaryContainerDark = Color(0xFF354B3E)
private val OnSecondaryContainerDark = Color(0xFFCEE9D7)

private val TertiaryDark = Color(0xFFA4CDDD)
private val OnTertiaryDark = Color(0xFF063542)
private val TertiaryContainerDark = Color(0xFF234C5A)
private val OnTertiaryContainerDark = Color(0xFFC0E9FA)

private val ErrorDark = Color(0xFFFFB4AB)
private val OnErrorDark = Color(0xFF690005)
private val ErrorContainerDark = Color(0xFF93000A)
private val OnErrorContainerDark = Color(0xFFFFDAD6)

private val BackgroundDark = Color(0xFF191C1A)
private val OnBackgroundDark = Color(0xFFE2E3DF)
private val SurfaceDark = Color(0xFF191C1A)
private val OnSurfaceDark = Color(0xFFE2E3DF)
private val SurfaceVariantDark = Color(0xFF404944)
private val OnSurfaceVariantDark = Color(0xFFBFC9C2)
private val OutlineDark = Color(0xFF8A938D)
private val OutlineVariantDark = Color(0xFF404944)
private val ScrimDark = Color(0xFF000000)
private val InverseSurfaceDark = Color(0xFFE2E3DF)
private val InverseOnSurfaceDark = Color(0xFF191C1A)
private val InversePrimaryDark = Color(0xFF1B6B4A)
private val SurfaceDimDark = Color(0xFF111412)
private val SurfaceBrightDark = Color(0xFF373A37)
private val SurfaceContainerLowestDark = Color(0xFF0C0F0D)
private val SurfaceContainerLowDark = Color(0xFF191C1A)
private val SurfaceContainerDark = Color(0xFF1D201E)
private val SurfaceContainerHighDark = Color(0xFF272B28)
private val SurfaceContainerHighestDark = Color(0xFF323633)

val ChiTieuLightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    inversePrimary = InversePrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceTint = PrimaryLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    scrim = ScrimLight,
    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
)

val ChiTieuDarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    inversePrimary = InversePrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceTint = PrimaryDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    scrim = ScrimDark,
    surfaceDim = SurfaceDimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
)
