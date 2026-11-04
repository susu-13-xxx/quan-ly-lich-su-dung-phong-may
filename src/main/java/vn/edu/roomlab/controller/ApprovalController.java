
package vn.edu.roomlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.edu.roomlab.service.BookingService;

@Controller
@RequestMapping("/admin/bookings")
public class ApprovalController {

    private final BookingService bookingService;

    public ApprovalController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Hiển thị danh sách yêu cầu chờ duyệt
    @GetMapping
    public String showPendingBookings(Model model) {

        model.addAttribute(
                "bookings",
                bookingService.getPendingBookings()
        );

        return "approvals";
    }

    // Duyệt yêu cầu
    @PostMapping("/{id}/approve")
    public String approveBooking(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            bookingService.approveBooking(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã duyệt yêu cầu đặt phòng thành công!"
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/bookings";
    }

    // Từ chối yêu cầu
    @PostMapping("/{id}/reject")
    public String rejectBooking(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            bookingService.rejectBooking(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã từ chối yêu cầu đặt phòng!"
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/bookings";
    }
}
