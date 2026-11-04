package vn.edu.roomlab.service;

import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.Booking;
import vn.edu.roomlab.entity.Room;
import vn.edu.roomlab.repository.BookingRepository;
import vn.edu.roomlab.repository.RoomRepository;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private static final DateTimeFormatter LABEL_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM");

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public ReportService(
            BookingRepository bookingRepository,
            RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    public Map<String, Object> buildReport(LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings =
                bookingRepository.findByBookingDateBetweenOrderByBookingDateAscStartTimeAsc(
                        startDate, endDate);
        List<Room> rooms = roomRepository.findAll();

        Map<LocalDate, Long> dailyCounts = new LinkedHashMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dailyCounts.put(date, 0L);
        }

        Map<String, Long> statusCounts = new LinkedHashMap<>();
        statusCounts.put("PENDING", 0L);
        statusCounts.put("APPROVED", 0L);
        statusCounts.put("REJECTED", 0L);
        statusCounts.put("CANCELLED", 0L);

        Map<Integer, Double> roomHours = new LinkedHashMap<>();
        Map<Integer, String> roomNames = new LinkedHashMap<>();
        for (Room room : rooms) {
            roomHours.put(room.getRoomId(), 0.0);
            roomNames.put(room.getRoomId(), room.getRoomName());
        }

        long approvedCount = 0;
        long pendingCount = 0;
        long approvedMinutes = 0;

        for (Booking booking : bookings) {
            dailyCounts.computeIfPresent(
                    booking.getBookingDate(), (date, count) -> count + 1);

            String status = booking.getStatus() == null
                    ? "UNKNOWN" : booking.getStatus().toUpperCase();
            statusCounts.computeIfPresent(status, (key, count) -> count + 1);

            if ("APPROVED".equals(status)) {
                approvedCount++;
                if (booking.getStartTime() != null && booking.getEndTime() != null) {
                    long minutes = Duration.between(
                            booking.getStartTime(), booking.getEndTime()).toMinutes();
                    approvedMinutes += minutes;
                    if (booking.getRoom() != null) {
                        Integer roomId = booking.getRoom().getRoomId();
                        roomHours.computeIfPresent(
                                roomId, (id, hours) -> hours + minutes / 60.0);
                    }
                }
            } else if ("PENDING".equals(status)) {
                pendingCount++;
            }
        }

        List<String> trendLabels = new ArrayList<>();
        List<Long> trendValues = new ArrayList<>();
        dailyCounts.forEach((date, count) -> {
            trendLabels.add(date.format(LABEL_FORMAT));
            trendValues.add(count);
        });

        List<String> roomLabels = new ArrayList<>();
        List<Double> roomValues = new ArrayList<>();
        roomNames.forEach((id, name) -> {
            roomLabels.add(name);
            roomValues.add(Math.round(roomHours.get(id) * 10.0) / 10.0);
        });

        List<String> statusLabels = List.of(
                "Chờ duyệt", "Đã duyệt", "Từ chối", "Đã hủy");
        List<Long> statusValues = List.of(
                statusCounts.get("PENDING"),
                statusCounts.get("APPROVED"),
                statusCounts.get("REJECTED"),
                statusCounts.get("CANCELLED"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalBookings", bookings.size());
        result.put("approvedBookings", approvedCount);
        result.put("pendingBookings", pendingCount);
        result.put("approvedHours", Math.round(approvedMinutes / 6.0) / 10.0);
        result.put("trendLabels", trendLabels);
        result.put("trendValues", trendValues);
        result.put("roomLabels", roomLabels);
        result.put("roomValues", roomValues);
        result.put("statusLabels", statusLabels);
        result.put("statusValues", statusValues);
        return result;
    }
}
