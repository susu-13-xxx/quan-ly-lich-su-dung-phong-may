# RoomLab Manager – bộ khung giao diện

## Công nghệ
- Java 17
- Spring Boot 3.4.4
- Thymeleaf (HTML phía server)
- PostgreSQL + pgAdmin 4
- Maven

## 1. Chuẩn bị
Cài JDK 17+, VS Code, Extension Pack for Java, Spring Boot Extension Pack và PostgreSQL/pgAdmin 4.
Có thể dùng Maven Wrapper nếu bổ sung wrapper, hoặc cài Maven và kiểm tra `mvn -v`.

## 2. Tạo database
Mở pgAdmin 4, kết nối PostgreSQL, mở Query Tool và chạy:
```sql
CREATE DATABASE room_lab_db;
```
Sau đó sửa `spring.datasource.username` và `spring.datasource.password` trong
`src/main/resources/application.properties` cho đúng máy của bạn.
Không đưa mật khẩu thật lên Git.

## 3. Chạy dự án
Mở thư mục `room-lab-manager` bằng VS Code, mở Terminal tại thư mục có `pom.xml`, chạy:
```bash
mvn spring-boot:run
```
Mở trình duyệt: http://localhost:8080

## 4. Các đường dẫn đã tạo
- `/login`
- `/dashboard`
- `/timetable`
- `/bookings/new`
- `/bookings/mine`
- `/admin/approvals`
- `/admin/rooms`
- `/admin/users`
- `/admin/reports`
- `/incidents`
- `/profile`

## Lưu ý quan trọng
Đây mới là **bộ khung giao diện và điều hướng**. Form đăng nhập hiện chỉ chuyển tới dashboard,
chưa xác thực tài khoản; menu Admin chưa được bảo vệ bằng phân quyền thật; các trang chưa có CRUD
hay dữ liệu nghiệp vụ. Không dùng bản này như hệ thống triển khai thực tế trước khi bổ sung
Spring Security, kiểm tra quyền ở backend, validation và xử lý dữ liệu.

## Gợi ý thứ tự phát triển tiếp
1. Thiết kế bảng `users`, `roles`, `rooms`, `equipment`, `bookings`, `incidents`.
2. Tạo Entity, Repository, Service và Controller cho từng nghiệp vụ.
3. Thêm Spring Security: đăng nhập, BCrypt, phân quyền ADMIN/LECTURER/STUDENT.
4. Làm chức năng gửi yêu cầu, duyệt lịch và kiểm tra trùng khung giờ.
5. Làm báo cáo và quản lý sự cố.
