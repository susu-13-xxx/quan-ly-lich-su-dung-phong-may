package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.User;
import vn.edu.roomlab.dto.UserResponse;
import vn.edu.roomlab.repository.UserRepository;
import vn.edu.roomlab.repository.RoleRepository;
import vn.edu.roomlab.entity.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lấy danh sách người dùng dưới dạng DTO trong transaction,
    // đảm bảo thông tin Role (LAZY) được đọc khi Hibernate Session còn mở.
    public List<UserResponse> getAllUserResponses() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getUserResponseById(Integer id) {
        User user = getUserById(id);
        return UserResponse.from(user);
    }

    // Tìm người dùng theo ID
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy người dùng có ID: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản đang đăng nhập."));
    }

    public void updateOwnProfile(String username, String fullName, String email, String phone) {
        User user = getUserByUsername(username);
        String normalizedEmail = email == null || email.isBlank() ? null : email.trim();

        if (normalizedEmail != null
                && userRepository.existsByEmailAndUserIdNot(normalizedEmail, user.getUserId())) {
            throw new IllegalArgumentException("Email này đã được tài khoản khác sử dụng.");
        }

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Họ và tên không được để trống.");
        }

        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPhone(phone == null || phone.isBlank() ? null : phone.trim());
        userRepository.save(user);
    }

    public void changeOwnPassword(String username, String currentPassword,
                                  String newPassword, String confirmPassword) {
        User user = getUserByUsername(username);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác.");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu hiện tại.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Thêm hoặc cập nhật người dùng
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Tạo tài khoản mới từ màn hình quản trị; mật khẩu luôn được mã hóa trước khi lưu.
    public User createUser(String username, String rawPassword, String fullName,
                           String email, String phone, Integer roleId) {
        String normalizedUsername = username.trim();
        String normalizedEmail = email == null || email.isBlank() ? null : email.trim();

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
        if (normalizedEmail != null && userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email đã được sử dụng.");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Vai trò không hợp lệ."));

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPhone(phone == null || phone.isBlank() ? null : phone.trim());
        user.setRole(role);
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Chỉ cho phép quản trị viên khóa/mở khóa tài khoản Teacher và Technician.
    public void updateUserStatus(Integer id, String status) {
        if (status == null || !List.of("ACTIVE", "INACTIVE").contains(status)) {
            throw new IllegalArgumentException("Trạng thái tài khoản không hợp lệ.");
        }

        User user = getUserById(id);
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "";

        if (!"TEACHER".equalsIgnoreCase(roleName)
                && !"TECHNICIAN".equalsIgnoreCase(roleName)) {
            throw new IllegalArgumentException(
                    "Chỉ được thay đổi trạng thái tài khoản Teacher và Technician.");
        }

        user.setStatus(status);
        userRepository.save(user);
    }

    // Xóa người dùng
    public void deleteUser(Integer id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}