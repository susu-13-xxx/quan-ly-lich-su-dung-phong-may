package vn.edu.roomlab.controller;

import java.time.LocalDate;
import java.security.Principal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.edu.roomlab.service.UserService;
import vn.edu.roomlab.service.RoleService;
import vn.edu.roomlab.service.IncidentService;
import vn.edu.roomlab.service.BookingService;
import vn.edu.roomlab.service.RoomService;

@Controller
public class PageController {

    private final UserService userService;
    private final RoleService roleService;
    private final IncidentService incidentService;
    private final BookingService bookingService;
    private final RoomService roomService;

    public PageController(
            UserService userService,
            RoleService roleService,
            IncidentService incidentService,
            BookingService bookingService,
            RoomService roomService
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.incidentService = incidentService;
        this.bookingService = bookingService;
        this.roomService = roomService;
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

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
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
    public String timetable(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String status,
            Model model
    ) {
        model.addAttribute("bookings", bookingService.getTimetable(date, roomId, status));
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("selectedStatus", status);

        return page(
                model,
                "Lịch biểu tổng quan",
                "Theo dõi và lọc lịch sử dụng các phòng máy theo ngày, phòng và trạng thái.",
                "timetable",
                "fragments/timetable"
        );
    }

    // NGƯỜI DÙNG VÀ PHÂN QUYỀN
    @GetMapping("/admin/users")
    public String users(@RequestParam(required = false) String created, Model model) {
        model.addAttribute("users", userService.getAllUserResponses());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("userCreated", created != null);

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
        model.addAttribute("incidents", incidentService.getAllIncidents());

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