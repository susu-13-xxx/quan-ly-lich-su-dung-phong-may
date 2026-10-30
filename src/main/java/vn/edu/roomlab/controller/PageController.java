package vn.edu.roomlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private String page(Model model, String title, String description, String active) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("pageDescription", description);
        model.addAttribute("activePage", active);
        return "layout";
    }

    @GetMapping("/")
    public String home() { return "redirect:/dashboard"; }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Đăng nhập / Phục hồi mật khẩu");
        model.addAttribute("pageDescription", "Khu vực đăng nhập và khôi phục mật khẩu.");
        model.addAttribute("activePage", "login");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return page(model, "Tổng quan", "Tổng quan nhanh về hoạt động sử dụng phòng máy.", "dashboard");
    }

    @GetMapping("/timetable")
    public String timetable(Model model) {
        return page(model, "Lịch biểu tổng quan", "Theo dõi lịch sử dụng các phòng máy theo ngày và khung giờ.", "timetable");
    }

    @GetMapping("/bookings/new")
    public String booking(Model model) {
        return page(model, "Đặt phòng máy", "Tạo yêu cầu đặt phòng máy cho buổi học hoặc hoạt động.", "booking");
    }

    @GetMapping("/bookings/mine")
    public String myBookings(Model model) {
        return page(model, "Lịch sử đặt phòng cá nhân", "Danh sách các yêu cầu đặt phòng của tài khoản hiện tại.", "myBookings");
    }

    @GetMapping("/admin/approvals")
    public String approvals(Model model) {
        return page(model, "Quản lý lịch đặt phòng", "Admin xem xét, duyệt hoặc từ chối yêu cầu đặt phòng.", "approvals");
    }

    @GetMapping("/admin/rooms")
    public String rooms(Model model) {
        return page(model, "Quản lý phòng máy & thiết bị", "Quản lý danh sách phòng, sức chứa và thiết bị.", "rooms");
    }

    @GetMapping("/admin/users")
    public String users(Model model) {
        return page(model, "Quản lý người dùng & phân quyền", "Quản lý tài khoản và vai trò truy cập.", "users");
    }

    @GetMapping("/admin/reports")
    public String reports(Model model) {
        return page(model, "Báo cáo hiệu suất sử dụng", "Thống kê tần suất và hiệu suất khai thác phòng máy.", "reports");
    }

    @GetMapping("/incidents")
    public String incidents(Model model) {
        return page(model, "Quản lý sự cố / Báo hỏng", "Gửi và theo dõi tình trạng thiết bị hoặc phòng máy gặp sự cố.", "incidents");
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        return page(model, "Thông tin cá nhân / Đổi mật khẩu", "Cập nhật thông tin hồ sơ và mật khẩu.", "profile");
    }
}
