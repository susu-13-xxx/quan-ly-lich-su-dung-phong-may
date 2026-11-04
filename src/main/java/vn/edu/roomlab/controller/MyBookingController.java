
package vn.edu.roomlab.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.edu.roomlab.entity.User;
import vn.edu.roomlab.repository.UserRepository;
import vn.edu.roomlab.service.BookingService;

@Controller
@RequestMapping("/bookings")
public class MyBookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    public MyBookingController(
            BookingService bookingService,
            UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/mine")
    public String myBookings(Principal principal, Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy tài khoản"));

        model.addAttribute(
                "bookings",
                bookingService.getMyBookings(user.getUserId())
        );

        model.addAttribute("pageTitle", "Lịch sử đặt phòng");
        model.addAttribute(
                "pageDescription",
                "Theo dõi các yêu cầu đặt phòng của bạn."
        );

        model.addAttribute("activePage", "myBookings");
        model.addAttribute("currentUser", user);

        return "my-bookings";
    }
}
