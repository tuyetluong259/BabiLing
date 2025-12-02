package com.example.babiling.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ui.theme.BalooThambi2Family

private val BackgroundColor = Color(0xFFB1E8C4)
private val HeaderColor = Color(0xFF717086)
private val InfoTextColor = Color(0xFF717086)
private val CardColor = Color.White
private val PlaceholderIconColor = Color(0xFFE57373)

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var isInterfaceEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = "Back",
                    tint = HeaderColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(80.dp),
                        tint = PlaceholderIconColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "User 123",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = BalooThambi2Family,
                color = HeaderColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = "@user1234567",
                fontSize = 14.sp,
                fontFamily = BalooThambi2Family,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileActionCard(
                title = "Chỉnh sửa hồ sơ",
                onClick = onEditClick,
                fontFamily = BalooThambi2Family
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                title = "Ngôn ngữ đang học: Tiếng Anh",
                fontFamily = BalooThambi2Family
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                title = "Độ tuổi: 4",
                fontFamily = BalooThambi2Family
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Giao diện ( Sáng/Tối )",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = BalooThambi2Family,
                        color = InfoTextColor
                    )

                    Switch(
                        checked = isInterfaceEnabled,
                        onCheckedChange = { isInterfaceEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF18C07A),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFBDBDBD)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogoutClick),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Đăng xuất",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = BalooThambi2Family,
                    color = InfoTextColor,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(title: String, fontFamily: androidx.compose.ui.text.font.FontFamily) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = fontFamily,
            color = InfoTextColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
@Composable
private fun ProfileActionCard(title: String, onClick: () -> Unit, fontFamily: androidx.compose.ui.text.font.FontFamily) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = fontFamily,
            color = InfoTextColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}