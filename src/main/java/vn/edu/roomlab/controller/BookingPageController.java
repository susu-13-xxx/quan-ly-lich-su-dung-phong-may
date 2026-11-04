package vn.edu.roomlab.controller;

import java.security.Principal;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.format.annotation.DateTimeFormat;

import vn.edu.roomlab.repository.RoomRepository;
import vn.edu.roomlab.service.BookingService;
import vn.edu.roomlab.dto.BookingSlotResponse;

@Controller
@RequestMapping("/bookings")
public class BookingPageController {
    
    private final BookingService bookingService;
    private final RoomRepository roomRepository;

    public BookingPageController(
            BookingService bookingService,
            RoomRepository roomRepository) {
        this.bookingService = bookingService;
        this.roomRepository = roomRepository;
    }
    // API lấy lịch đặt phòng theo ngày
    @GetMapping("/availability")
    @ResponseBody
    public List<BookingSlotResponse> getRoomSchedule(
            @RequestParam Integer roomId,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate bookingDate) {

        return bookingService.getRoomSchedule(
            roomId,
            bookingDate
    );
}
    // Hiển thị biểu mẫu đặt phòng
    @GetMapping("/new")
    public String showBookingForm(
            @RequestParam(required = false) Integer roomId,
            Principal principal,
            Model model) {

        prepareForm(model, principal);

        model.addAttribute("selectedRoomId", roomId);

        return "booking-form";
    }

    // Xử lý gửi yêu cầu đặt phòng
    @PostMapping("/create")
    public String createBooking(
            @RequestParam Integer roomId,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate bookingDate,

            @RequestParam
            @DateTimeFormat(pattern = "HH:mm")
            LocalTime startTime,

            @RequestParam
            @DateTimeFormat(pattern = "HH:mm")
            LocalTime endTime,

            @RequestParam(required = false) String purpose,

            Principal principal,
            Model model) {

        try {
            bookingService.createBooking(
                    principal.getName(),
                    roomId,
                    bookingDate,
                    startTime,
                    endTime,
                    purpose
            );

            prepareForm(model, principal);

            model.addAttribute(
                    "successMessage",
                    "Tạo yêu cầu đặt phòng thành công! Vui lòng chờ duyệt."
            );

        } catch (RuntimeException e) {

            prepareForm(model, principal);

            model.addAttribute("errorMessage", e.getMessage());

            model.addAttribute("selectedRoomId", roomId);
            model.addAttribute("bookingDate", bookingDate);
            model.addAttribute("startTime", startTime);
            model.addAttribute("endTime", endTime);
            model.addAttribute("purpose", purpose);
        }

        return "booking-form";
    }

    // Chuẩn bị dữ liệu cho biểu mẫu
    private void prepareForm(Model model, Principal principal) {

        model.addAttribute("rooms", roomRepository.findAll());

        model.addAttribute(
                "username",
                principal != null ? principal.getName() : "Người dùng"
        );
    }
}