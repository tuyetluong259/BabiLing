package com.example.babiling.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ui.screens.progress.ProgressScreen
import com.example.babiling.ui.screens.topic.quiz.QuizScreen
import com.example.babiling.ui.theme.*

// Enum để quản lý các mục trong Bottom Nav
enum class HomeNavItems {
    Home, Rank, Learn, Settings
}

@Composable
fun HomeScreen(
    currentScreen: HomeNavItems,
    onNavigateToTopicSelect: () -> Unit,
    // ✨ 1. SỬA CHỮ KÝ HÀM TẠI ĐÂY ✨
    onNavigateToQuiz: (topicId: String) -> Unit,
    onNavigateToGame: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onBottomNavItemSelected: (HomeNavItems) -> Unit
) {
    Scaffold(
        bottomBar = {
            HomeBottomNavBar(
                currentScreen = currentScreen,
                onScreenSelected = { selectedScreen ->
                    if (selectedScreen == HomeNavItems.Settings) {
                        onNavigateToSettings()
                    } else {
                        onBottomNavItemSelected(selectedScreen)
                    }
                }
            )
        }
    ) { innerPadding ->
        when (currentScreen) {
            HomeNavItems.Home -> {
                HomeScreenContent(
                    paddingValues = innerPadding,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToTopicSelect = onNavigateToTopicSelect,
                    onNavigateToGame = onNavigateToGame,
                    onNavigateToQuiz = onNavigateToQuiz // Truyền hàm đã được sửa xuống
                )
            }
            HomeNavItems.Rank -> {
                ProgressScreen(paddingValues = innerPadding)
            }
            HomeNavItems.Learn -> {
                QuizScreen(
                    paddingValues = innerPadding,
                    topicId = null, // Chế độ ôn tập chung
                    onNavigateBack = {
                        onBottomNavItemSelected(HomeNavItems.Home)
                    }
                )
            }
            HomeNavItems.Settings -> { /* No-op */ }
        }
    }
}

@Composable
fun HomeScreenContent(
    paddingValues: PaddingValues,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToTopicSelect: () -> Unit,
    onNavigateToGame: () -> Unit,
    // ✨ 2. SỬA CHỮ KÝ HÀM Ở ĐÂY NỮA ✨
    onNavigateToQuiz: (topicId: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPink)
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        item {
            Column {
                HomeHeader(
                    onSettingsClick = onNavigateToSettings,
                    onProfileClick = onNavigateToProfile
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .offset(y = (-10).dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TopicCard(onClick = onNavigateToTopicSelect)
                    GameCard(onClick = onNavigateToGame)
                    // ✨ 3. CẬP NHẬT CÁCH GỌI Ở ĐÂY ✨
                    // Khi nhấn vào thẻ "Ôn tập", gọi hàm với topicId là "all"
                    ReviewCard(onClick = { onNavigateToQuiz("all") })
                }
                AdsCard(modifier = Modifier.offset(y = (30).dp))
            }
        }
    }
}

// --- CÁC HÀM COMPOSABLE KHÁC GIỮ NGUYÊN ---
// (HomeHeader, TopicCard, GameCard, ReviewCard, AdsCard, HomeBottomNavBar, BottomNavItem...)

@Composable
fun HomeHeader(onSettingsClick: () -> Unit, onProfileClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Chiều cao cố định cho header
    ) {
        // 1. Nền xanh lượn sóng
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Header Background",
            modifier = Modifier
                .matchParentSize()
                .offset(y = (-110).dp),
            contentScale = ContentScale.FillBounds
        )

        // 2. Các nút Cài đặt và Hồ sơ ở trên cùng
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, end = 24.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, "Cài đặt", tint = Color.White)
            }
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, "Hồ sơ", tint = Color.White)
            }
        }

        // 3. Dòng chữ "Chào mừng..."
        Text(
            text = "Chào mừng đến với\nBabiLing!!!",
            fontFamily = BalooThambiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = TextHome,
            lineHeight = 40.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = (-30).dp)
                .padding(top = 100.dp, start = 24.dp)
        )

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 20.dp)
                .size(110.dp)
        )

        // 5. Cây nấm trang trí
        Image(
            painter = painterResource(id = R.drawable.decor4),
            contentDescription = "Trang trí",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(y = (30).dp)
                .padding(start = 32.dp, bottom = 40.dp)
                .size(90.dp)
        )
    }
}


@Composable
fun TopicCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // ✨ Đặt chiều cao cố định
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Chọn chủ đề",
                fontFamily = BalooThambiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf(R.drawable.a, R.drawable.b, R.drawable.c).forEach { iconRes ->
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(horizontal = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GameCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // ✨ Đặt chiều cao cố định
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Trò chơi",
                fontFamily = BalooThambiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black,
                modifier = androidx.compose.ui.Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.trochoiminhhoa),
                contentDescription = "Trò chơi",
                modifier = Modifier
                    .height(60.dp)
                    .width(170.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Composable
fun ReviewCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // ✨ Đặt chiều cao cố định
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ôn tập",
                fontFamily = BalooThambiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf(R.drawable.ontap1, R.drawable.ontap2, R.drawable.ontap3).forEach { iconRes ->
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .height(58.dp)
                            .width(60.dp)
                            .padding(horizontal = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ads),
            contentDescription = "Quảng cáo",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun HomeBottomNavBar(
    currentScreen: HomeNavItems,
    onScreenSelected: (HomeNavItems) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5BE)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    iconRes = R.drawable.home1,
                    screen = HomeNavItems.Home,
                    isSelected = currentScreen == HomeNavItems.Home,
                    onClick = { onScreenSelected(HomeNavItems.Home) }
                )
                BottomNavItem(
                    iconRes = R.drawable.home2,
                    screen = HomeNavItems.Rank,
                    isSelected = currentScreen == HomeNavItems.Rank,
                    onClick = { onScreenSelected(HomeNavItems.Rank) }
                )
                BottomNavItem(
                    iconRes = R.drawable.home3,
                    screen = HomeNavItems.Learn,
                    isSelected = currentScreen == HomeNavItems.Learn,
                    onClick = { onScreenSelected(HomeNavItems.Learn) }
                )
                BottomNavItem(
                    iconRes = R.drawable.home4,
                    screen = HomeNavItems.Settings,
                    isSelected = currentScreen == HomeNavItems.Settings,
                    onClick = { onScreenSelected(HomeNavItems.Settings) }
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    @DrawableRes iconRes: Int,
    screen: HomeNavItems,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFF8A80) else Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = screen.name,
                tint = Color.Unspecified, // Sử dụng màu gốc của icon
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
