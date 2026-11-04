package vn.edu.roomlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.edu.roomlab.service.RoleService;
import vn.edu.roomlab.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class UserPageController {

    private final UserService userService;
    private final RoleService roleService;

    public UserPageController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        prepareForm(model);
        return "layout";
    }

    @PostMapping("/create")
    public String createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam Integer roleId,
            Model model) {

        try {
            if (username.isBlank() || password.isBlank() || fullName.isBlank()) {
                throw new IllegalArgumentException("Vui lòng nhập đầy đủ tên đăng nhập, mật khẩu và họ tên.");
            }
            if (password.length() < 8) {
                throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
            }

            userService.createUser(username, password, fullName, email, phone, roleId);
            return "redirect:/admin/users?created";
        } catch (IllegalArgumentException e) {
            prepareForm(model);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            model.addAttribute("selectedRoleId", roleId);
            return "layout";
        }
    }

    private void prepareForm(Model model) {
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("pageTitle", "Thêm người dùng mới");
        model.addAttribute("pageDescription", "Tạo tài khoản và cấp vai trò truy cập cho người dùng.");
        model.addAttribute("activePage", "users");
        model.addAttribute("content", "fragments/user-create");
    }
}
