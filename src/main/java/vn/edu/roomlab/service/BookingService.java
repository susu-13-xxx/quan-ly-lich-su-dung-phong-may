package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.Booking;
import vn.edu.roomlab.repository.BookingRepository;
import vn.edu.roomlab.dto.BookingResponse;

import java.util.List;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    public BookingResponse getBookingById(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy lịch đặt phòng có ID: " + id));

        return BookingResponse.from(booking);
    }

    public BookingResponse saveBooking(Booking booking) {
        return BookingResponse.from(
                bookingRepository.save(booking)
        );
    }

    public void deleteBooking(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy lịch đặt phòng có ID: " + id));

        bookingRepository.delete(booking);
    }
}