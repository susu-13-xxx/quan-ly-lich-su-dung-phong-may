package vn.edu.roomlab.dto;

import java.time.LocalDateTime;

import vn.edu.roomlab.entity.User;

/**
 * Du lieu nguoi dung tra ve cho API.
 * Khong bao gom password de tranh lo thong tin dang nhap.
 */
public record UserResponse(
        Integer userId,
        String username,
        String fullName,
        String email,
        String phone,
        Integer roleId,
        String roleName,
        String status,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole() != null ? user.getRole().getRoleId() : null,
                user.getRole() != null ? user.getRole().getRoleName() : null,
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
