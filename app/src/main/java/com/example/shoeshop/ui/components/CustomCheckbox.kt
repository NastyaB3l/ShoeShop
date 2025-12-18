// components/CustomCheckbox.kt
package com.example.shoeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.shoeshop.R
import com.example.shoeshop.ui.theme.LocalCustomTypography

@Composable
fun CustomCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    val customTypography = LocalCustomTypography.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(MaterialTheme.shapes.small)
                .selectable(
                    selected = isChecked,
                    onClick = { onCheckedChange(!isChecked) },
                    role = Role.Checkbox
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(
                        width = 2.dp,
                        color = if (isChecked) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                        shape = MaterialTheme.shapes.small
                    )
                    .background(
                        if (isChecked) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.background
                    )
            )

            if (isChecked) {
                Icon(
                    painter = painterResource(id = R.drawable.policy_check),
                    contentDescription = "Выбрано",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = customTypography.displayMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.weight(1f)
        )
    }
}