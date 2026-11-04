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

    // Xóa người dùng
    public void deleteUser(Integer id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}