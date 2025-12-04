# BabiLing - Ứng Dụng Học Ngôn Ngữ Cho Trẻ Em
<p align="center"><img src="app/src/main/res/drawable/logo.png" alt="Logo BabiLing" width="200"/></p>

* BabiLing là một ứng dụng di động học ngôn ngữ được thiết kế đặc biệt cho trẻ em, xây dựng hoàn toàn bằng công nghệ hiện đại của Android. Với giao diện thân thiện, nội dung học tập phong phú và các hoạt động tương tác, BabiLing giúp trẻ em tiếp cận ngôn ngữ mới một cách tự nhiên và hiệu quả.Dự án được xây dựng bằng Kotlin và Jetpack Compose, tuân thủ theo kiến trúc MVVM (Model-View-ViewModel) để đảm bảo mã nguồn dễ dàng bảo trì, mở rộng và kiểm thử.
# ✨ Các Chức Năng Chính
- Học qua Chủ đề: Bài học được phân loại theo các chủ đề gần gũi với trẻ em như động vật, trái cây, phương tiện giao thông, giúp việc học trở nên thú vị và dễ liên tưởng.
- Flashcards Tương tác: Trẻ học từ vựng mới thông qua flashcards có hình ảnh minh họa sinh động và âm thanh phát âm chuẩn.
- Bài kiểm tra (Quiz): Các bài quiz ngắn sau mỗi bài học giúp củng cố kiến thức và tạo sự hứng thú cho trẻ.
- Theo dõi tiến độ: Ứng dụng tự động lưu lại tiến trình học tập của người dùng, đồng bộ hóa giữa các thiết bị thông qua tài khoản.
- Xác thực người dùng: Hỗ trợ đăng ký, đăng nhập bằng Email/Mật khẩu và đăng nhập nhanh qua tài khoản Google.
- Quản lý tài khoản: Người dùng có thể xem và chỉnh sửa thông tin cá nhân, quản lý các cài đặt bảo mật.
- Đồng bộ hóa dữ liệu: Dữ liệu tiến trình học tập được lưu trữ cục bộ (offline-first) bằng Room và đồng bộ lên Cloud Firestore khi có kết nối mạng.
# 🛠️ Công Nghệ và Kiến Trúc
***Dự án được xây dựng trên một nền tảng công nghệ vững chắc, bao gồm:*** 
- Ngôn ngữ: Kotlin•Giao diện người dùng (UI): Jetpack Compose - Bộ công cụ UI hiện đại của Android.
- Kiến trúc: MVVM (Model-View-ViewModel).
- Xử lý bất đồng bộ: Kotlin Coroutines & Flow.
- Điều hướng (Navigation): Jetpack Navigation for Compose.
- Lưu trữ cục bộ (Local Database): Room - Hỗ trợ truy cập offline và cache dữ liệu.
- Dịch vụ Backend: Firebase
  - Firebase Authentication: Xác thực người dùng.
  - Cloud Firestore: Lưu trữ và đồng bộ dữ liệu tiến trình.
  - Firebase Storage: Lưu trữ các tài nguyên media (nếu có).
  - Firebase App Check: Bảo vệ backend khỏi các truy cập trái phép.
- Dependency Injection: Hướng tiếp cận thủ công với ServiceLocator.
- Tải ảnh: Coil - Thư viện tải ảnh được tối ưu cho Jetpack Compose.
- Quản lý phiên bản: Version Catalog (libs.versions.toml).
# 📂 Cấu Trúc Dự Án
```BabiLing/
├── app/
│   ├── src/main/
│   │├── java/com/example/babiling/
│   │   │   ├── data/                 # Lớp Repository, Model, và nguồn dữ liệu (Local/Remote)
│   │   │   │   ├── local/            # Room Database, DAO
│   │   │   │   ├── remote/           # Tương tác với Firebase
│   │   │   │   └── repository/       # Trung tâm điều phối dữ liệu
│   │   │   ├── ui/                   # Các thành phần liên quan đến UI
│   │   │   │   ├── screens/          # Các màn hình chính của ứng dụng (Auth, Home, Profile...)
│   │   │   │   ├── theme/            # Theme, Color, Typography
│   │   │   │   └── navigation/       # Định nghĩa các route và luồng điều hướng
│   │   │   ├── MainActivity.kt       # Activity chính, nơi chứa NavHost
│   │   │   └── ServiceLocator.kt     # Cung cấp các dependency cho ứng dụng
│   │   └── res/                      # Tài nguyên (drawable, font, string...)
│   └── build.gradle.kts              # File build của module app
├── build.gradle.kts                  # File build của toàn dự án
└── gradle/libs.versions.toml         # Version Catalog để quản lý thư viện 
```
* # 🚀 Cài Đặt và Chạy Thử
**Để build và chạy thử dự án, bạn cần thực hiện các bước sau:**
_Yêu cầu:_
Android Studio Iguana | 2023.2.1 hoặc mới hơn.
JDK 17.Các bước cài đặt:
**_1.Clone Repository:_**
git clone [(https://github.com/tuyetluong259/BabiLing.git)]
* cd BabiLing
**_2.Kết nối với Firebase:_**
- Truy cập Firebase Console.
- Tạo một dự án Firebase mới.
- Thêm một ứng dụng Android vào dự án Firebase với package name là com.example.babiling.
- Tải về file google-services.json và đặt nó vào thư mục app/.
- Trong Firebase Console, kích hoạt các dịch vụ sau:
  - Authentication: Bật phương thức đăng nhập bằng Email/Password và Google.
  - Firestore Database: Tạo một database ở chế độ production.
  - Storage: (Nếu cần) Tạo một bucket lưu trữ.
**_3.Build Dự Án:_**
- Mở dự án bằng Android Studio.
- Android Studio sẽ tự động đồng bộ Gradle. Quá trình này có thể mất vài phút.
- Nếu gặp lỗi org.gradle.java.home, hãy vào File -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle và chọn một Gradle JDK là jbr-17 hoặc Embedded JDK 17.
**_4.Chạy Ứng Dụng:_**
- Kết nối một thiết bị Android thật hoặc khởi động một máy ảo (Emulator).
- Nhấn nút Run 'app' (▶️) trên thanh công cụ của Android Studio.
**Để cài đặt về điện thoại:**
- Quét mã qr và tải xuống
<p align="center"><img src="app/src/main/res/drawable/qrcode_babiling.png" alt="Logo BabiLing" width="400"/></p>
