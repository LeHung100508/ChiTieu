package hung.lct.chitieu.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hung.lct.chitieu.core.designsystem.theme.ChiTieuTheme
import hung.lct.chitieu.core.designsystem.theme.summaryAmount

@Composable
fun SummaryCard(
    title: String,
    amount: Long,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    progress: Float? = null,
    progressLabel: String? = null,
    isPrimary: Boolean = false,
) {
    val containerColor = if (isPrimary) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    val contentColor = if (isPrimary) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            AmountText(amount = amount, style = MaterialTheme.typography.summaryAmount)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (progress != null) {
                SummaryProgressBar(
                    progress = progress,
                    label = progressLabel,
                )
            }
        }
    }
}

@Composable
private fun SummaryProgressBar(
    progress: Float,
    label: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(MaterialTheme.shapes.extraSmall),
            color = MaterialTheme.colorScheme.primary,
            trackColor = LocalContentColor.current.copy(alpha = 0.14f),
            strokeCap = StrokeCap.Round,
        )
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun SummaryCardLightPreview() {
    ChiTieuTheme(darkTheme = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SummaryCard(
                title = "Đã chi tháng này",
                amount = 5000000,
                subtitle = "Hôm nay 110.000 đ",
                progress = 0.65f,
                progressLabel = "65% ngân sách",
                isPrimary = true,
            )
            SummaryCard(
                title = "Tiền nhà tháng 10",
                amount = 2500000,
                subtitle = "Đã thanh toán",
            )
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SummaryCardDarkPreview() {
    ChiTieuTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SummaryCard(
                title = "Đã chi tháng này",
                amount = 5000000,
                subtitle = "Hôm nay 110.000 đ",
                progress = 0.65f,
                progressLabel = "65% ngân sách",
                isPrimary = true,
            )
            SummaryCard(
                title = "Tiền nhà tháng 10",
                amount = 2500000,
                subtitle = "Đã thanh toán",
            )
        }
    }
}
