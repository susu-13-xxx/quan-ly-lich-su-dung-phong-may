package vn.edu.roomlab.controller;

import org.springframework.web.bind.annotation.*;

import vn.edu.roomlab.entity.Booking;
import vn.edu.roomlab.service.BookingService;
import vn.edu.roomlab.dto.BookingResponse;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponse> getAll() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public BookingResponse getById(@PathVariable Integer id) {
        return bookingService.getBookingById(id);
    }

    @PostMapping
    public BookingResponse create(@RequestBody Booking booking) {
        return bookingService.saveBooking(booking);
    }

    @PutMapping("/{id}")
    public BookingResponse update(
            @PathVariable Integer id,
            @RequestBody Booking booking) {
        booking.setBookingId(id);
        return bookingService.saveBooking(booking);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        bookingService.deleteBooking(id);
    }
}