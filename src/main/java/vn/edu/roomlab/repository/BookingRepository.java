
package vn.edu.roomlab.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.roomlab.entity.Booking;

public interface BookingRepository
        extends JpaRepository<Booking, Integer> {

    // Lấy danh sách đặt phòng của một người dùng
    List<Booking> findByUser_UserIdOrderByCreatedAtDesc(
            Integer userId);

    // Lấy danh sách yêu cầu theo trạng thái
    List<Booking> findByStatusOrderByCreatedAtAsc(
            String status);

    // Kiểm tra trùng lịch PENDING hoặc APPROVED
    @Query("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.room.roomId = :roomId
          AND b.bookingDate = :bookingDate
          AND b.status IN ('PENDING', 'APPROVED')
          AND b.startTime < :endTime
          AND b.endTime > :startTime
    """)
    boolean existsOverlappingBooking(
            @Param("roomId") Integer roomId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // Kiểm tra trùng với lịch đã được duyệt,
    // loại trừ chính yêu cầu đang xét
    @Query("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.room.roomId = :roomId
          AND b.bookingDate = :bookingDate
          AND b.status = 'APPROVED'
          AND b.bookingId <> :bookingId
          AND b.startTime < :endTime
          AND b.endTime > :startTime
    """)
    boolean existsApprovedOverlappingBooking(
            @Param("roomId") Integer roomId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("bookingId") Integer bookingId
    );

    // Lấy lịch đã đặt của một phòng trong ngày
    List<Booking>
    findByRoom_RoomIdAndBookingDateAndStatusInOrderByStartTimeAsc(
            Integer roomId,
            LocalDate bookingDate,
            List<String> statuses
    );

    // Lọc lịch biểu theo ngày, phòng và trạng thái.
    @Query("""
        SELECT b
        FROM Booking b
        JOIN FETCH b.room
        JOIN FETCH b.user
        WHERE (:bookingDate IS NULL OR b.bookingDate = :bookingDate)
          AND (:roomId IS NULL OR b.room.roomId = :roomId)
          AND (:status IS NULL OR b.status = :status)
        ORDER BY b.bookingDate ASC, b.startTime ASC
    """)
    List<Booking> findForTimetable(
            @Param("bookingDate") LocalDate bookingDate,
            @Param("roomId") Integer roomId,
            @Param("status") String status
    );
}
