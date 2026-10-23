package hung.lct.chitieu.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hung.lct.chitieu.core.designsystem.theme.ChiTieuTheme

@Composable
fun CategoryIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(color = containerColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun CategoryIconLightPreview() {
    ChiTieuTheme(darkTheme = false) {
        Row(modifier = Modifier.padding(16.dp)) {
            CategoryIcon(
                icon = Icons.Default.ShoppingCart,
                contentDescription = "Mua sắm",
            )
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun CategoryIconDarkPreview() {
    ChiTieuTheme(darkTheme = true) {
        Row(modifier = Modifier.padding(16.dp)) {
            CategoryIcon(
                icon = Icons.Default.ShoppingCart,
                contentDescription = "Mua sắm",
            )
        }
    }
}
