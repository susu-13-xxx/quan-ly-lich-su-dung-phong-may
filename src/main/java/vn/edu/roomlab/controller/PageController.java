
package vn.edu.roomlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.edu.roomlab.service.UserService;
import vn.edu.roomlab.service.RoleService;
import vn.edu.roomlab.service.IncidentService;

@Controller
public class PageController {

    private final UserService userService;
    private final RoleService roleService;
    private final IncidentService incidentService;

    public PageController(
            UserService userService,
            RoleService roleService,
            IncidentService incidentService
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.incidentService = incidentService;
    }

    private String page(
            Model model,
            String title,
            String description,
            String active,
            String content
    ) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("pageDescription", description);
        model.addAttribute("activePage", active);
        model.addAttribute("content", content);

        return "layout";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle",
                "Đăng nhập / Phục hồi mật khẩu");
        model.addAttribute("pageDescription",
                "Khu vực đăng nhập và khôi phục mật khẩu.");
        model.addAttribute("activePage", "login");

        return "login";
    }

    // TỔNG QUAN
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return page(
                model,
                "Tổng quan",
                "Tổng quan nhanh về hoạt động sử dụng phòng máy.",
                "dashboard",
                "fragments/dashboard"
        );
    }

    // LỊCH BIỂU
    @GetMapping("/timetable")
    public String timetable(Model model) {
        return page(
                model,
                "Lịch biểu tổng quan",
                "Theo dõi lịch sử dụng các phòng máy theo ngày và khung giờ.",
                "timetable",
                "fragments/timetable"
        );
    }

    // NGƯỜI DÙNG VÀ PHÂN QUYỀN
    @GetMapping("/admin/users")
    public String users(Model model) {

        model.addAttribute(
            "users",
            userService.getAllUserResponses()
        );

        model.addAttribute(
            "roles",
            roleService.getAllRoles()
        );

        return page(
            model,
            "Quản lý người dùng & phân quyền",
            "Quản lý tài khoản và vai trò truy cập.",
            "users",
            "fragments/users"
        );
    }

    // BÁO CÁO HIỆU SUẤT
    @GetMapping("/admin/reports")
    public String reports(Model model) {
        return page(
                model,
                "Báo cáo hiệu suất sử dụng",
                "Thống kê tần suất và hiệu suất khai thác phòng máy.",
                "reports",
                "fragments/reports"
        );
    }

    // SỰ CỐ / BÁO HỎNG
    @GetMapping("/incidents")
    public String incidents(Model model) {

        model.addAttribute(
            "incidents",
            incidentService.getAllIncidents()
        );

        return page(
            model,
            "Quản lý sự cố / Báo hỏng",
            "Gửi và theo dõi tình trạng thiết bị hoặc phòng máy gặp sự cố.",
            "incidents",
            "fragments/incidents"
        );
    }

    // THÔNG TIN CÁ NHÂN
    @GetMapping("/profile")
    public String profile(Model model) {
        return page(
                model,
                "Thông tin cá nhân / Đổi mật khẩu",
                "Cập nhật thông tin hồ sơ và mật khẩu.",
                "profile",
                "fragments/profile"
        );
    }
}
