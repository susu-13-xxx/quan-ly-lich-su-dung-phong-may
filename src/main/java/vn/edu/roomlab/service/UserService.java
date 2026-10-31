package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.User;
import vn.edu.roomlab.dto.UserResponse;
import vn.edu.roomlab.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    // Xóa người dùng
    public void deleteUser(Integer id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}