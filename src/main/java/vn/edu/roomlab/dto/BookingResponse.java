package vn.edu.roomlab.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import vn.edu.roomlab.entity.Booking;

public record BookingResponse(
        Integer bookingId,
        Integer userId,
        Integer roomId,
        LocalDate bookingDate,
        LocalTime startTime,
        LocalTime endTime,
        String purpose,
        String status,
        LocalDateTime createdAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getUser() != null
                        ? booking.getUser().getUserId() : null,
                booking.getRoom() != null
                        ? booking.getRoom().getRoomId() : null,
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getPurpose(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}