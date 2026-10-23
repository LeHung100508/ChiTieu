package hung.lct.chitieu.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hung.lct.chitieu.core.designsystem.theme.ChiTieuTheme

@Composable
fun ChiTieuChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Medium,
            )
        },
        modifier = modifier,
        leadingIcon = leadingIcon,
        shape = MaterialTheme.shapes.small,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isHighlighted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                Color.Transparent
            },
            labelColor = if (isHighlighted) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = if (isHighlighted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            },
            borderWidth = if (isHighlighted) 1.5.dp else 1.dp,
        ),
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun ChiTieuChipLightPreview() {
    ChiTieuTheme(darkTheme = false) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChiTieuChip("Ăn uống", onClick = {})
            ChiTieuChip("Cà phê", onClick = {}, isHighlighted = true)
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ChiTieuChipDarkPreview() {
    ChiTieuTheme(darkTheme = true) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChiTieuChip("Ăn uống", onClick = {})
            ChiTieuChip("Cà phê", onClick = {}, isHighlighted = true)
        }
    }
}
