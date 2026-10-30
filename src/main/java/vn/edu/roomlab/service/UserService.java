package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.User;
import vn.edu.roomlab.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Lấy danh sách người dùng
    public List<User> getAllUsers() {
        return userRepository.findAll();
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