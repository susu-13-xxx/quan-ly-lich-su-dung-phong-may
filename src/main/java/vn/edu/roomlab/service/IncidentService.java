package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.dto.IncidentRequest;
import vn.edu.roomlab.dto.IncidentResponse;
import vn.edu.roomlab.entity.Equipment;
import vn.edu.roomlab.entity.Incident;
import vn.edu.roomlab.entity.Room;
import vn.edu.roomlab.entity.User;
import vn.edu.roomlab.repository.EquipmentRepository;
import vn.edu.roomlab.repository.IncidentRepository;
import vn.edu.roomlab.repository.RoomRepository;
import vn.edu.roomlab.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;

    private static final Set<String> VALID_STATUSES =
            Set.of("OPEN", "IN_PROGRESS", "RESOLVED");

    public IncidentService(
            IncidentRepository incidentRepository,
            UserRepository userRepository,
            RoomRepository roomRepository,
            EquipmentRepository equipmentRepository
    ) {
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public List<IncidentResponse> getAllIncidents() {
        return incidentRepository.findAll()
                .stream()
                .map(IncidentResponse::from)
                .toList();
    }

    public IncidentResponse getIncidentById(Integer id) {
        Incident incident = findIncident(id);
        return IncidentResponse.from(incident);
    }

    // Tạo sự cố mới
    public IncidentResponse createIncident(
            IncidentRequest request,
            String username
    ) {
        validateRequest(request);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy tài khoản"));

        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phòng máy"));

        if (request.equipmentType() != null
                && !"AIR_CONDITIONER".equals(request.equipmentType())) {
            throw new IllegalArgumentException("Loại thiết bị báo hỏng không hợp lệ");
        }
        if (request.equipmentType() != null && request.equipmentId() != null) {
            throw new IllegalArgumentException(
                    "Chỉ chọn thiết bị thường hoặc điều hòa, không chọn đồng thời");
        }

        Equipment equipment = request.equipmentType() != null
                ? findOrCreateBrokenAirConditioner(room)
                : findEquipment(request.equipmentId());

        if (equipment != null
                && !equipment.getRoom().getRoomId().equals(room.getRoomId())) {
            throw new RuntimeException(
                    "Thiết bị không thuộc phòng máy đã chọn");
        }

        Incident incident = new Incident();

        if (equipment != null) {
            equipment.setStatus("BROKEN");
            equipmentRepository.save(equipment);
        }

        incident.setUser(user);
        Equipment previousEquipment = incident.getEquipment();
        incident.setRoom(room);
        incident.setEquipment(equipment);
        incident.setTitle(request.title().trim());
        incident.setDescription(request.description());
        incident.setPriority(request.priority());
        incident.setStatus("OPEN");
        incident.setReportedAt(LocalDateTime.now());
        incident.setResolvedAt(null);

        return IncidentResponse.from(
                incidentRepository.save(incident)
        );
    }

    // Tạo hồ sơ điều hòa nếu phòng chưa có; nếu đã có thì đánh dấu đang hỏng.
    private Equipment findOrCreateBrokenAirConditioner(Room room) {
        Equipment equipment = equipmentRepository
                .findFirstByRoom_RoomIdAndEquipmentNameIgnoreCase(
                        room.getRoomId(), "Điều hòa")
                .orElseGet(() -> {
                    Equipment created = new Equipment();
                    created.setEquipmentName("Điều hòa");
                    created.setEquipmentType("AIR_CONDITIONER");
                    created.setRoom(room);
                    created.setQuantity(1);
                    created.setDescription("Thiết bị điều hòa không khí của phòng.");
                    created.setCreatedAt(LocalDateTime.now());
                    return created;
                });

        equipment.setStatus("BROKEN");
        return equipmentRepository.save(equipment);
    }

    // Cập nhật thông tin và trạng thái sự cố
    public IncidentResponse updateIncident(
            Integer id,
            IncidentRequest request
    ) {
        Incident incident = findIncident(id);

        validateRequest(request);

        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phòng máy"));

        Equipment equipment = findEquipment(request.equipmentId());

        if (equipment != null
                && !equipment.getRoom().getRoomId().equals(room.getRoomId())) {
            throw new RuntimeException(
                    "Thiết bị không thuộc phòng máy đã chọn");
        }

        if (request.status() == null
                || !VALID_STATUSES.contains(request.status())) {
            throw new RuntimeException("Trạng thái sự cố không hợp lệ");
        }

        incident.setRoom(room);
        incident.setEquipment(equipment);
        incident.setTitle(request.title().trim());
        incident.setDescription(request.description());
        incident.setPriority(request.priority());
        incident.setStatus(request.status());

        if ("RESOLVED".equals(request.status())) {
            if (incident.getResolvedAt() == null) {
                incident.setResolvedAt(LocalDateTime.now());
            }
        } else {
            incident.setResolvedAt(null);
        }

        Incident saved = incidentRepository.save(incident);
        syncEquipmentStatus(previousEquipment);
        syncEquipmentStatus(equipment);

        return IncidentResponse.from(saved);
    }

    // Chỉ cập nhật trạng thái, không cho phép sửa nội dung báo cáo gốc.
    public void updateIncidentStatus(Integer id, String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Trạng thái sự cố không hợp lệ");
        }

        Incident incident = findIncident(id);
        incident.setStatus(status);

        if ("RESOLVED".equals(status)) {
            if (incident.getResolvedAt() == null) {
                incident.setResolvedAt(LocalDateTime.now());
            }
        } else {
            incident.setResolvedAt(null);
        }

        incidentRepository.save(incident);
        syncEquipmentStatus(incident.getEquipment());
    }

    private void syncEquipmentStatus(Equipment equipment) {
        if (equipment == null) {
            return;
        }

        boolean hasActiveIncident = incidentRepository.findByStatusIn(
                List.of("OPEN", "IN_PROGRESS")
        ).stream().anyMatch(item ->
                item.getEquipment() != null
                        && item.getEquipment().getEquipmentId()
                                .equals(equipment.getEquipmentId()));

        equipment.setStatus(hasActiveIncident ? "BROKEN" : "WORKING");
        equipmentRepository.save(equipment);
    }

    public void deleteIncident(Integer id) {
        Incident incident = findIncident(id);
        Equipment equipment = incident.getEquipment();
        incidentRepository.delete(incident);
        syncEquipmentStatus(equipment);
    }

    private Incident findIncident(Integer id) {
        return incidentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy sự cố có ID: " + id));
    }

    private Equipment findEquipment(Integer equipmentId) {
        if (equipmentId == null) {
            return null;
        }

        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy thiết bị"));
    }

    private void validateRequest(IncidentRequest request) {
        if (request == null) {
            throw new RuntimeException("Dữ liệu sự cố không hợp lệ");
        }

        if (request.roomId() == null) {
            throw new RuntimeException("Vui lòng chọn phòng máy");
        }

        if (request.title() == null || request.title().isBlank()) {
            throw new RuntimeException("Vui lòng nhập tiêu đề sự cố");
        }

        if (request.priority() == null || request.priority().isBlank()) {
            throw new RuntimeException("Vui lòng chọn mức độ ưu tiên");
        }

        if (!Set.of("LOW", "MEDIUM", "HIGH")
                .contains(request.priority())) {
            throw new RuntimeException("Mức độ ưu tiên không hợp lệ");
        }
    }
}
