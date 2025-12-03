package com.example.babiling.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
    onNavigateToQuiz: (topicId: String) -> Unit,
    onNavigateToGame: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,

    // ✅ THÊM THAM SỐ ĐIỀU HƯỚNG BỊ THIẾU TỪ MainActivity
    onNavigateToProgress: () -> Unit,

    onBottomNavItemSelected: (HomeNavItems) -> Unit
) {
    Scaffold(
        bottomBar = {
            HomeBottomNavBar(
                currentScreen = currentScreen,
                onScreenSelected = { selectedScreen ->
                    // Phân luồng: Nếu là Settings thì điều hướng riêng, còn lại thì đổi màn hình chính
                    if (selectedScreen == HomeNavItems.Settings) {
                        onNavigateToSettings()
                    } else {
                        onBottomNavItemSelected(selectedScreen)
                    }
                }
            )
        }
    ) { innerPadding ->
        // Dựa vào `currentScreen` để hiển thị nội dung tương ứng
        when (currentScreen) {
            HomeNavItems.Home -> {
                HomeScreenContent(
                    paddingValues = innerPadding,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToTopicSelect = onNavigateToTopicSelect,
                    onNavigateToGame = onNavigateToGame,
                    onNavigateToQuiz = onNavigateToQuiz
                )
            }
            HomeNavItems.Rank -> {
                // Màn hình Xếp hạng (Tiến trình)
                ProgressScreen(paddingValues = innerPadding)
            }
            HomeNavItems.Learn -> {
                // Màn hình Ôn tập chung
                QuizScreen(
                    paddingValues = innerPadding,
                    topicId = null,
                    // ✅ SỬA LỖI: Cần một hành động để xử lý khi QuizScreen hoàn tất (onNavigateBack)
                    onNavigateBack = {
                        // Quay về tab Home sau khi ôn tập xong
                        onBottomNavItemSelected(HomeNavItems.Home)
                    }
                )
            }
            // Không cần xử lý HomeNavItems.Settings ở đây vì nó đã được xử lý ở bottomBar
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
    onNavigateToQuiz: (topicId: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPink)
    ) {
        item {
            Column {
                HomeHeader(
                    onSettingsClick = onNavigateToSettings,
                    onProfileClick = onNavigateToProfile
                )
                // Các thẻ chức năng
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .offset(y = (-10).dp), // Kéo các thẻ lên một chút để đè lên header
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TopicCard(onClick = onNavigateToTopicSelect)
                    GameCard(onClick = onNavigateToGame)
                    // Khi nhấn vào thẻ "Ôn tập", gọi hàm với topicId đặc biệt là "all" (hoặc null tùy logic của bạn)
                    ReviewCard(onClick = { onNavigateToQuiz("all") })
                }
                AdsCard(modifier = Modifier.offset(y = 30.dp)) // Thẻ quảng cáo
            }
        }
        item {
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding() + 40.dp))
        }
    }
}

// ... (Các Composable HomeHeader, TopicCard, GameCard, ReviewCard, AdsCard, HomeBottomNavBar, BottomNavItem giữ nguyên) ...

@Composable
fun HomeHeader(onSettingsClick: () -> Unit, onProfileClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Header Background",
            modifier = Modifier
                .matchParentSize()
                .offset(y = (-130).dp), // Đã bỏ offset(y = (-130).dp) để hình nền không bị cắt
            contentScale = ContentScale.FillBounds
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 20.dp, end = 20.dp), // Thêm padding top an toàn
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, "Cài đặt", tint = Color.White)
            }
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, "Hồ sơ", tint = Color.White)
            }
        }
        Text(
            text = "Chào mừng đến với\nBabiLing!!!",
            fontFamily = BalooThambiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = TextHome,
            lineHeight = 40.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 60.dp, start = 24.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 40.dp)
                .size(110.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.decor4),
            contentDescription = "Trang trí",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(y = 30.dp)
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
                .height(72.dp)
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
                .height(72.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Trò chơi",
                fontFamily = BalooThambiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
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
                .height(72.dp)
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
    Image(        painter = painterResource(id = R.drawable.ads),
        contentDescription = "Quảng cáo",
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.FillWidth
    )
}


@Composable
fun HomeBottomNavBar(
    currentScreen: HomeNavItems,
    onScreenSelected: (HomeNavItems) -> Unit
) {
    NavigationBar(
        containerColor = TextHome,
        modifier = Modifier.height(80.dp)
    ) {
        BottomNavItem(
            label = "Trang chủ",
            iconRes = R.drawable.home1,
            isSelected = currentScreen == HomeNavItems.Home,
            onClick = { onScreenSelected(HomeNavItems.Home) }
        )
        BottomNavItem(
            label = "Tiến độ",
            iconRes = R.drawable.home2,
            isSelected = currentScreen == HomeNavItems.Rank,
            onClick = { onScreenSelected(HomeNavItems.Rank) }
        )
        BottomNavItem(
            label = "Ôn tập",
            iconRes = R.drawable.home3,
            isSelected = currentScreen == HomeNavItems.Learn,
            onClick = { onScreenSelected(HomeNavItems.Learn) }
        )
        BottomNavItem(
            label = "Cài đặt",
            iconRes = R.drawable.home4,
            isSelected = currentScreen == HomeNavItems.Settings,
            onClick = { onScreenSelected(HomeNavItems.Settings) }
        )
    }
}

@Composable
fun RowScope.BottomNavItem(    label: String,
                               @DrawableRes iconRes: Int,
                               isSelected: Boolean,
                               onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp, fontFamily = BalooThambiFamily) },
        icon = {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                // Thêm dòng này để Icon hiển thị đúng màu gốc của ảnh
                tint = Color.Unspecified
            )
        },
        colors = NavigationBarItemDefaults.colors(
            // Các màu này giờ sẽ chỉ áp dụng cho chữ (Text) và màu nền khi được chọn (indicator)
            selectedTextColor = IndigoBlue,
            unselectedTextColor = Color.Gray,
            indicatorColor = BrightGreen
        )
    )
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Trạng thái giả để chạy Preview
    var currentScreen by remember { mutableStateOf(HomeNavItems.Home) }

    BabiLingTheme { // Bọc trong Theme của bạn để có style đúng
        HomeScreen(
            currentScreen = currentScreen,
            onNavigateToTopicSelect = { },
            onNavigateToQuiz = { },
            onNavigateToGame = { },
            onNavigateToSettings = { },
            onNavigateToProfile = { },
            // Cung cấp tham số mới
            onNavigateToProgress = { },
            onBottomNavItemSelected = { newScreen ->
                currentScreen = newScreen
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun BottomNavPreview() {
    BabiLingTheme {
        HomeBottomNavBar(
            currentScreen = HomeNavItems.Home,
            onScreenSelected = {}
        )
    }
}