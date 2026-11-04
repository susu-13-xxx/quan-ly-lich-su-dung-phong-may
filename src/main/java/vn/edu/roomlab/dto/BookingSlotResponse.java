package vn.edu.roomlab.dto;

import java.time.format.DateTimeFormatter;

import vn.edu.roomlab.entity.Booking;

public class BookingSlotResponse {

    private String startTime;
    private String endTime;
    private String status;

    public BookingSlotResponse(Booking booking) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm");

        this.startTime = booking.getStartTime().format(formatter);
        this.endTime = booking.getEndTime().format(formatter);
        this.status = booking.getStatus();
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }
}