package vn.edu.roomlab.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.Booking;
import vn.edu.roomlab.entity.Incident;
import vn.edu.roomlab.entity.Room;
import vn.edu.roomlab.entity.User;
import vn.edu.roomlab.repository.BookingRepository;
import vn.edu.roomlab.repository.IncidentRepository;
import vn.edu.roomlab.repository.RoomRepository;
import vn.edu.roomlab.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    public DashboardService(BookingRepository bookingRepository,
                            RoomRepository roomRepository,
                            IncidentRepository incidentRepository,
                            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> buildDashboard(String username,
                                               boolean admin,
                                               boolean technician) {
        LocalDate today = LocalDate.now();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản."));

        List<Room> rooms = roomRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAll();
        List<Incident> allIncidents = incidentRepository.findAll();

        List<Booking> visibleBookings = (admin || technician) ? allBookings
                : allBookings.stream()
                    .filter(b -> b.getUser() != null
                            && b.getUser().getUserId().equals(currentUser.getUserId()))
                    .toList();

        List<Incident> visibleIncidents = (admin || technician) ? allIncidents
                : allIncidents.stream()
                    .filter(i -> i.getUser() != null
                            && i.getUser().getUserId().equals(currentUser.getUserId()))
                    .toList();

        long todayBookings = visibleBookings.stream()
                .filter(b -> today.equals(b.getBookingDate())).count();
        long pendingBookings = visibleBookings.stream()
                .filter(b -> "PENDING".equalsIgnoreCase(b.getStatus())).count();
        long approvedBookings = visibleBookings.stream()
                .filter(b -> "APPROVED".equalsIgnoreCase(b.getStatus())).count();
        long activeIncidents = visibleIncidents.stream()
                .filter(i -> !"RESOLVED".equalsIgnoreCase(i.getStatus())).count();
        long highPriorityIncidents = visibleIncidents.stream()
                .filter(i -> "HIGH".equalsIgnoreCase(i.getPriority())
                        && !"RESOLVED".equalsIgnoreCase(i.getStatus())).count();

        List<DashboardBooking> upcomingBookings = visibleBookings.stream()
                .filter(b -> b.getBookingDate() != null
                        && !b.getBookingDate().isBefore(today)
                        && ("PENDING".equalsIgnoreCase(b.getStatus())
                            || "APPROVED".equalsIgnoreCase(b.getStatus())))
                .sorted(Comparator.comparing(Booking::getBookingDate)
                        .thenComparing(Booking::getStartTime,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(6)
                .map(this::toDashboardBooking)
                .toList();

        List<DashboardBooking> recentBookings = visibleBookings.stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(this::toDashboardBooking)
                .toList();

        List<DashboardIncident> recentIncidents = visibleIncidents.stream()
                .sorted(Comparator.comparing(Incident::getReportedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(this::toDashboardIncident)
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("dashboardName", currentUser.getFullName());
        data.put("dashboardDate", today);
        data.put("isAdmin", admin);
        data.put("isSystemView", admin || technician);
        data.put("isTechnician", technician);
        data.put("totalRooms", rooms.size());
        data.put("availableRooms", rooms.stream()
                .filter(r -> "AVAILABLE".equalsIgnoreCase(r.getStatus())).count());
        data.put("todayBookings", todayBookings);
        data.put("pendingBookings", pendingBookings);
        data.put("approvedBookings", approvedBookings);
        data.put("activeIncidents", activeIncidents);
        data.put("highPriorityIncidents", highPriorityIncidents);
        data.put("myBookingCount", visibleBookings.size());
        data.put("totalUsers", admin ? userRepository.count() : null);
        data.put("upcomingBookings", upcomingBookings);
        data.put("recentBookings", recentBookings);
        data.put("recentIncidents", recentIncidents);
        return data;
    }

    private DashboardBooking toDashboardBooking(Booking booking) {
        return new DashboardBooking(
                booking.getBookingId(),
                booking.getRoom() != null ? booking.getRoom().getRoomName() : "—",
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus()
        );
    }

    private DashboardIncident toDashboardIncident(Incident incident) {
        return new DashboardIncident(
                incident.getIncidentId(),
                incident.getTitle(),
                incident.getRoom() != null ? incident.getRoom().getRoomName() : "—",
                incident.getPriority(),
                incident.getStatus(),
                incident.getReportedAt()
        );
    }

    public record DashboardBooking(Integer id, String roomName,
                                   LocalDate bookingDate,
                                   java.time.LocalTime startTime,
                                   java.time.LocalTime endTime,
                                   String status) {}

    public record DashboardIncident(Integer id, String title, String roomName,
                                     String priority, String status,
                                     LocalDateTime reportedAt) {}
}
