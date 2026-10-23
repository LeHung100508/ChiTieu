package hung.lct.chitieu.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import hung.lct.chitieu.core.designsystem.theme.ChiTieuTheme
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

fun formatVnd(
    amount: Long,
    includePrefix: Boolean = false,
    type: AmountType = AmountType.EXPENSE,
    locale: Locale = Locale.getDefault(),
): String {
    val formatter = NumberFormat.getNumberInstance(locale)
    val formatted = formatter.format(abs(amount)) + " ₫"

    return when {
        includePrefix && type == AmountType.INCOME -> "+$formatted"
        includePrefix && type == AmountType.EXPENSE -> "\u2212$formatted"
        else -> formatted
    }
}

@Composable
fun AmountText(
    amount: Long,
    modifier: Modifier = Modifier,
    type: AmountType = AmountType.EXPENSE,
    color: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    includePrefix: Boolean = false,
) {
    val locale = LocalConfiguration.current.locales[0]
    val formattedText = remember(amount, includePrefix, type, locale) {
        formatVnd(amount, includePrefix, type, locale)
    }
    val textColor = if (color != Color.Unspecified) {
        color
    } else {
        when (type) {
            AmountType.INCOME -> MaterialTheme.colorScheme.primary
            AmountType.EXPENSE -> LocalContentColor.current
            AmountType.TRANSFER -> MaterialTheme.colorScheme.secondary
        }
    }

    Text(
        text = formattedText,
        style = style,
        color = textColor,
        modifier = modifier,
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun AmountTextLightPreview() {
    ChiTieuTheme(darkTheme = false) {
        Column {
            AmountText(amount = 1000000, type = AmountType.INCOME, includePrefix = true)
            AmountText(amount = 1000000, type = AmountType.EXPENSE, includePrefix = true)
            AmountText(amount = 1000000, type = AmountType.TRANSFER, includePrefix = true)
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun AmountTextDarkPreview() {
    ChiTieuTheme(darkTheme = true) {
        Column {
            AmountText(amount = 1000000, type = AmountType.INCOME, includePrefix = true)
            AmountText(amount = 1000000, type = AmountType.EXPENSE, includePrefix = true)
            AmountText(amount = 1000000, type = AmountType.TRANSFER, includePrefix = true)
        }
    }
}
