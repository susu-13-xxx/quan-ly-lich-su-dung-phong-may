
package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import vn.edu.roomlab.entity.Booking;
import vn.edu.roomlab.entity.Room;
import vn.edu.roomlab.entity.User;

import vn.edu.roomlab.repository.BookingRepository;
import vn.edu.roomlab.repository.RoomRepository;
import vn.edu.roomlab.repository.UserRepository;

import vn.edu.roomlab.dto.BookingResponse;
import vn.edu.roomlab.dto.BookingSlotResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            RoomRepository roomRepository) {

        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    // Lấy tất cả lịch đặt phòng
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    
    // Lấy danh sách yêu cầu đang chờ duyệt
    public List<BookingResponse> getPendingBookings() {
        return bookingRepository
                .findByStatusOrderByCreatedAtAsc("PENDING")
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    // Duyệt yêu cầu đặt phòng
    public void approveBooking(Integer id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy yêu cầu đặt phòng"));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new RuntimeException(
                    "Yêu cầu này đã được xử lý trước đó");
        }

        if (!"AVAILABLE".equalsIgnoreCase(
                booking.getRoom().getStatus())) {
            throw new RuntimeException(
                    "Phòng hiện không khả dụng hoặc đang bảo trì");
        }

        boolean overlapping =
                bookingRepository.existsApprovedOverlappingBooking(
                        booking.getRoom().getRoomId(),
                        booking.getBookingDate(),
                        booking.getStartTime(),
                        booking.getEndTime(),
                        booking.getBookingId()
                );

        if (overlapping) {
            throw new RuntimeException(
                    "Không thể duyệt: phòng đã có lịch được duyệt trùng giờ");
        }

        booking.setStatus("APPROVED");
        bookingRepository.save(booking);
    }

    // Từ chối yêu cầu đặt phòng
    public void rejectBooking(Integer id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy yêu cầu đặt phòng"));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new RuntimeException(
                    "Yêu cầu này đã được xử lý trước đó");
        }

        booking.setStatus("REJECTED");
        bookingRepository.save(booking);
    }

    // Lấy lịch đặt phòng theo ID
    public BookingResponse getBookingById(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy lịch đặt phòng có ID: " + id));

        return BookingResponse.from(booking);
    }

    // Lưu lịch đặt phòng
    public BookingResponse saveBooking(Booking booking) {
        return BookingResponse.from(
                bookingRepository.save(booking)
        );
    }

    // Xóa lịch đặt phòng
    public void deleteBooking(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy lịch đặt phòng có ID: " + id));

        bookingRepository.delete(booking);
    }

    // Tạo yêu cầu đặt phòng mới
    public BookingResponse createBooking(
            String username,
            Integer roomId,
            LocalDate bookingDate,
            LocalTime startTime,
            LocalTime endTime,
            String purpose) {

        // Tìm người dùng đang đăng nhập
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy tài khoản người dùng"));

        // Tìm phòng được chọn
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy phòng máy"));
        if (!"AVAILABLE".equalsIgnoreCase(room.getStatus())) {
            throw new RuntimeException(
            "Phòng máy hiện không khả dụng hoặc đang bảo trì");
}

        // Kiểm tra dữ liệu đầu vào
        
        if (startTime == null || endTime == null) {
            throw new RuntimeException("Vui lòng chọn giờ bắt đầu và giờ kết thúc");
        }

        if (!endTime.isAfter(startTime)) {
            throw new RuntimeException("Giờ kết thúc phải sau giờ bắt đầu");
        }

        LocalTime openingTime = LocalTime.of(6, 45);
        LocalTime closingTime = LocalTime.of(21, 30);

        if (startTime.isBefore(openingTime)
                || endTime.isAfter(closingTime)) {
            throw new RuntimeException(
                    "Phòng máy chỉ hoạt động từ 06:45 đến 21:30");
        }
        if (bookingDate == null || bookingDate.isBefore(LocalDate.now())) {
            throw new RuntimeException(
                    "Ngày đặt phòng không hợp lệ");
        }
        if (!isValidPeriodRange(startTime, endTime)) {
            throw new RuntimeException(
                    "Vui lòng chọn khung giờ theo các tiết học quy định");
        }

        if (bookingDate.equals(LocalDate.now())
                && !startTime.isAfter(LocalTime.now())) {
            throw new RuntimeException(
                    "Giờ đặt phòng phải nằm trong tương lai");
        }

        // Kiểm tra trùng lịch
        boolean isOverlapping =
                bookingRepository.existsOverlappingBooking(
                        roomId,
                        bookingDate,
                        startTime,
                        endTime
                );

        if (isOverlapping) {
            throw new RuntimeException(
                    "Phòng đã có yêu cầu đặt trong khoảng thời gian này");
        }

        // Tạo yêu cầu đặt phòng
        Booking booking = new Booking();

        booking.setUser(user);
        booking.setRoom(room);
        booking.setBookingDate(bookingDate);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPurpose(purpose);
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());

        return BookingResponse.from(
                bookingRepository.save(booking)
        );
    }

    // Lấy danh sách đặt phòng của người dùng
    public List<BookingResponse> getMyBookings(Integer userId) {
        return bookingRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    // Lấy lịch đã đặt theo phòng và ngày
    public List<BookingSlotResponse> getRoomSchedule(

            Integer roomId,
            LocalDate bookingDate) {

        return bookingRepository
            .findByRoom_RoomIdAndBookingDateAndStatusInOrderByStartTimeAsc(
                    roomId,
                    bookingDate,
                    List.of("PENDING", "APPROVED")
            )
            .stream()
            .map(BookingSlotResponse::new)
            .toList();
}

    private boolean isValidPeriodRange(
            LocalTime startTime,
            LocalTime endTime) {

        List<LocalTime> starts = List.of(
            LocalTime.of(6, 45),
            LocalTime.of(7, 30),
            LocalTime.of(8, 25),
            LocalTime.of(9, 20),
            LocalTime.of(10, 15),
            LocalTime.of(11, 0),
            LocalTime.of(12, 30),
            LocalTime.of(13, 15),
            LocalTime.of(14, 10),
            LocalTime.of(15, 5),
            LocalTime.of(16, 0),
            LocalTime.of(16, 45),
            LocalTime.of(17, 45),
            LocalTime.of(18, 30),
            LocalTime.of(19, 15),
            LocalTime.of(20, 0),
            LocalTime.of(20, 45)
        );

        List<LocalTime> ends = List.of(
            LocalTime.of(7, 30),
            LocalTime.of(8, 15),
            LocalTime.of(9, 10),
            LocalTime.of(10, 5),
            LocalTime.of(11, 0),
            LocalTime.of(11, 45),
            LocalTime.of(13, 15),
            LocalTime.of(14, 0),
            LocalTime.of(14, 55),
            LocalTime.of(15, 50),
            LocalTime.of(16, 45),
            LocalTime.of(17, 30),
            LocalTime.of(18, 30),
            LocalTime.of(19, 15),
            LocalTime.of(20, 0),
            LocalTime.of(20, 45),
            LocalTime.of(21, 30)
        );

        int startIndex = starts.indexOf(startTime);
        int endIndex = ends.indexOf(endTime);

        return startIndex >= 0  
                && endIndex >= startIndex;
    }


    // Chỉ thêm điều kiện khi người dùng thực sự chọn bộ lọc.
    @Transactional(readOnly = true)
    public List<Booking> getTimetable(LocalDate bookingDate, Integer roomId, String status) {
        Specification<Booking> specification = (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (bookingDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("bookingDate"), bookingDate));
            }

            if (roomId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.join("room").get("roomId"), roomId));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status.trim()));
            }

            return criteriaBuilder.and(predicates.toArray(
                    new jakarta.persistence.criteria.Predicate[0]));
        };

        return bookingRepository.findAll(
                specification,
                Sort.by(Sort.Order.asc("bookingDate"), Sort.Order.asc("startTime"))
        );
    }
}
