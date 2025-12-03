package com.example.babiling.ui.screens.settings.account

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.babiling.ui.theme.BalooThambi2Family

@Composable
fun ConfirmationDialog(
    primaryColor: Color,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    titleText: String,
    bodyText: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = titleText,
                fontFamily = BalooThambi2Family,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF03A9F4)
            )
        },
        text = {
            Text(
                text = bodyText,
                fontFamily = BalooThambi2Family,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    "Đồng ý",
                    fontFamily = BalooThambi2Family,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Hủy",
                    fontFamily = BalooThambi2Family,
                    color = Color.Gray
                )
            }
        }
    )
}