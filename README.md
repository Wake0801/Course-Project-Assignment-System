# Hệ thống phân công đề tài môn học

Ứng dụng web hỗ trợ quản lý, phân công và theo dõi đề tài học phần trong khối lớp tín chỉ — thay thế quy trình thủ công, giảm trùng lặp đề tài và tăng minh bạch khi theo dõi tiến độ, điểm số.

**Báo cáo thực tập cơ sở** — Học viện Công nghệ Bưu chính Viễn thông, Khoa Công nghệ Thông tin II.

| | |
|---|---|
| **Đề tài** | Xây dựng phần mềm phân công đề tài môn học cho sinh viên |
| **Sinh viên** | Lương Trương Đại (N22DCCN117), Vũ Phạm Minh Thức (N22DCVT099), Nguyễn Minh Quân (N22DCDK071) |
| **GV hướng dẫn** | Nguyễn Thị Bích Nguyên |

## Công nghệ sử dụng

| Lớp | Công nghệ |
|-----|-----------|
| Backend | Java 21, **Spring Boot 3.2**, Spring Web, Spring Data JPA (Hibernate) |
| Bảo mật | **Spring Security** (form login, phân quyền theo vai trò), BCrypt, giới hạn phiên (một phiên đăng nhập) |
| Giao diện | **Thymeleaf**, HTML/CSS, JavaScript (tương tác phía client) |
| CSDL | **Microsoft SQL Server** (JDBC `mssql-jdbc`) |
| Khác | Spring Mail (SMTP, hỗ trợ quên mật khẩu), Spring Boot Actuator, Spring Session (có tùy chọn Redis trong phụ thuộc), Validation |

Đóng gói: **`war`** — có thể triển khai trên Tomcat hoặc chạy trực tiếp bằng Spring Boot.

## Vai trò người dùng 
- **Sinh viên**: trang cá nhân, nhóm, đề tài, điểm; tham gia nhóm theo form do giảng viên tạo; (theo quy trình báo cáo) gửi đơn chuyển nhóm, v.v.
- **Giảng viên** (`/client/gv/**`): danh sách sinh viên, nhóm, giao/ quản lý đề tài, nhập điểm, xử lý đơn theo nghiệp vụ.
- **Nhân viên phòng khảo thí / quản trị** (`/admin/**`): quản lý sinh viên, giảng viên, nhân viên PKT, tài khoản, nhóm, đề tài — đồng bộ với nghiệp vụ tổng hợp điểm, lịch báo cáo, báo cáo thống kê (mô tả chi tiết trong báo cáo Word).

Phân quyền cấu hình tại `SecurityConfig` (ví dụ: `SINH_VIEN`, `GIANG_VIEN`, `NHAN_VIEN`, `ADMIN`).

## Cấu trúc mã nguồn (gói chính)

```
src/main/java/com/example/myproject/
├── MyprojectApplication.java
├── config/          # Security, đăng nhập / đăng xuất tùy chỉnh
├── controller/      # Admin, Client (GV/SV), đề tài, nhóm, tài khoản, ...
├── dto/
├── entity/          # Khoa, Lớp, Sinh viên, GV, LTC, Đề tài, Nhóm, Đơn, Điểm, ...
├── repository/
├── service/
└── compositeKey/    # Khóa phức hợp JPA

src/main/resources/
├── application.properties
└── templates/       # Thymeleaf: admin/, client/, other/
```

## Yêu cầu môi trường

- JDK **21**
- Maven 3.x
- SQL Server (cục bộ hoặc remote) và **database** đã tạo theo schema dự án (ví dụ tên CSDL trong cấu hình mẫu: `QLPhanCongDeTai`)
- (Tùy chọn) Tài khoản SMTP để gửi email quên mật khẩu

## Cấu hình & chạy ứng dụng

1. Clone repository và mở thư mục dự án.
2. Tạo CSDL SQL Server và import / chạy script schema (theo thiết kế trong báo cáo — ERD, từ điển dữ liệu).
3. Chỉnh `src/main/resources/application.properties`:
   - `spring.datasource.url`, `username`, `password` trỏ tới SQL Server của bạn.
   - `spring.mail.*` nếu cần tính năng gửi mail (nên dùng biến môi trường hoặc file cấu hình ngoài repo, **không** commit mật khẩu thật).
4. Chạy:

```bash
mvn spring-boot:run
```

Mặc định ứng dụng lắng nghe cổng **8080** (`server.port=8080`).

5. Truy cập trình duyệt: `http://localhost:8080` — đăng nhập theo tài khoản đã seed trong CSDL (trang login: `/login`, xử lý: `/process-login`).

**Lưu ý JPA:** `spring.jpa.hibernate.ddl-auto=none` — schema do DBA/script quản lý, không tự tạo bảng từ entity.


## Giấy phép & đóng góp

Dự án phục vụ mục đích học tập / báo cáo thực tập. Nếu mở rộng, nên tách cấu hình nhạy cảm (DB, mail) ra biến môi trường hoặc `application-local.properties` (đưa vào `.gitignore`).
