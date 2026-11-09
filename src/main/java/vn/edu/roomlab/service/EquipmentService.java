package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.Equipment;
import vn.edu.roomlab.entity.Incident;
import vn.edu.roomlab.entity.Room;
import vn.edu.roomlab.repository.EquipmentRepository;
import vn.edu.roomlab.repository.IncidentRepository;
import vn.edu.roomlab.repository.RoomRepository;
import vn.edu.roomlab.dto.EquipmentResponse;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final RoomRepository roomRepository;
    private final IncidentRepository incidentRepository;

    private static final Set<String> VALID_STATUSES =
            Set.of("WORKING", "BROKEN", "MAINTENANCE");
    private static final Set<String> ACTIVE_INCIDENT_STATUSES =
            Set.of("OPEN", "IN_PROGRESS");

    public EquipmentService(
            EquipmentRepository equipmentRepository,
            RoomRepository roomRepository,
            IncidentRepository incidentRepository) {
        this.equipmentRepository = equipmentRepository;
        this.roomRepository = roomRepository;
        this.incidentRepository = incidentRepository;
    }

    public List<EquipmentResponse> getAllEquipments() {
        return equipmentRepository.findAll()
                .stream()
                .map(EquipmentResponse::from)
                .toList();
    }

    // OPEN và IN_PROGRESS đều đồng nghĩa với thiết bị chưa thể sử dụng.
    public List<EquipmentResponse> getAllBrokenEquipments() {
        Map<Integer, Equipment> unavailable = new LinkedHashMap<>();

        equipmentRepository.findByStatus("BROKEN").forEach(
                equipment -> unavailable.put(equipment.getEquipmentId(), equipment));

        incidentRepository.findByStatusIn(ACTIVE_INCIDENT_STATUSES).stream()
                .map(Incident::getEquipment)
                .filter(equipment -> equipment != null)
                .forEach(equipment ->
                        unavailable.put(equipment.getEquipmentId(), equipment));

        return unavailable.values().stream()
                .map(EquipmentResponse::from)
                .toList();
    }

    public List<EquipmentResponse> getBrokenEquipmentsByRoomId(Integer roomId) {
        return getAllBrokenEquipments().stream()
                .filter(equipment -> roomId.equals(equipment.roomId()))
                .toList();
    }

    public EquipmentResponse getEquipmentById(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thiết bị có ID: " + id));
        return EquipmentResponse.from(equipment);
    }

    public EquipmentResponse saveEquipment(Equipment equipment) {
        if (equipment.getEquipmentName() == null
                || equipment.getEquipmentName().isBlank()) {
            throw new IllegalArgumentException("Tên thiết bị không được để trống");
        }
        if (equipment.getEquipmentType() == null
                || equipment.getEquipmentType().isBlank()) {
            throw new IllegalArgumentException("Loại thiết bị không được để trống");
        }
        if (equipment.getQuantity() == null || equipment.getQuantity() < 1) {
            throw new IllegalArgumentException("Số lượng thiết bị phải lớn hơn 0");
        }
        if (equipment.getStatus() == null
                || !VALID_STATUSES.contains(equipment.getStatus())) {
            throw new IllegalArgumentException("Trạng thái thiết bị không hợp lệ");
        }
        if (equipment.getRoom() == null || equipment.getRoom().getRoomId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn phòng");
        }

        Integer roomId = equipment.getRoom().getRoomId();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy phòng có ID: " + roomId));
        equipment.setRoom(room);

        Equipment saved;
        if (equipment.getEquipmentId() == null) {
            equipment.setCreatedAt(LocalDateTime.now());
            saved = equipmentRepository.save(equipment);
        } else {
            Equipment existing = equipmentRepository.findById(equipment.getEquipmentId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy thiết bị có ID: " + equipment.getEquipmentId()));
            existing.setEquipmentName(equipment.getEquipmentName());
            existing.setEquipmentType(equipment.getEquipmentType());
            existing.setRoom(room);
            existing.setQuantity(equipment.getQuantity());
            existing.setStatus(equipment.getStatus());
            existing.setDescription(equipment.getDescription());
            saved = equipmentRepository.save(existing);
        }
        return EquipmentResponse.from(saved);
    }

    public void deleteEquipment(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thiết bị có ID: " + id));
        equipmentRepository.delete(equipment);
    }
}
