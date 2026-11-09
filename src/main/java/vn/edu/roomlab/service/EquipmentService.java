package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.Equipment;
import vn.edu.roomlab.entity.Room;
import vn.edu.roomlab.repository.EquipmentRepository;
import vn.edu.roomlab.repository.RoomRepository;
import vn.edu.roomlab.dto.EquipmentResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final RoomRepository roomRepository;

    private static final Set<String> VALID_STATUSES =
            Set.of("WORKING", "BROKEN", "MAINTENANCE");

    public EquipmentService(
            EquipmentRepository equipmentRepository,
            RoomRepository roomRepository) {
        this.equipmentRepository = equipmentRepository;
        this.roomRepository = roomRepository;
    }

    // Lấy danh sách thiết bị
    public List<EquipmentResponse> getAllEquipments() {
        return equipmentRepository.findAll()
                .stream()
                .map(EquipmentResponse::from)
                .toList();
    }

    // Lấy thiết bị đang hỏng của tất cả phòng
    public List<EquipmentResponse> getAllBrokenEquipments() {
        return equipmentRepository.findByStatus("BROKEN")
                .stream()
                .map(EquipmentResponse::from)
                .toList();
    }

    // Lấy thiết bị đang hỏng của một phòng
    public List<EquipmentResponse> getBrokenEquipmentsByRoomId(Integer roomId) {
        return equipmentRepository.findByRoom_RoomIdAndStatus(roomId, "BROKEN")
                .stream()
                .map(EquipmentResponse::from)
                .toList();
    }

    // Lấy chi tiết thiết bị
    public EquipmentResponse getEquipmentById(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thiết bị có ID: " + id));

        return EquipmentResponse.from(equipment);
    }

    // Thêm hoặc cập nhật thiết bị
    public EquipmentResponse saveEquipment(Equipment equipment) {

        if (equipment.getEquipmentName() == null
                || equipment.getEquipmentName().isBlank()) {
            throw new IllegalArgumentException(
                    "Tên thiết bị không được để trống");
        }

        if (equipment.getEquipmentType() == null
                || equipment.getEquipmentType().isBlank()) {
            throw new IllegalArgumentException(
                    "Loại thiết bị không được để trống");
        }

        if (equipment.getQuantity() == null
                || equipment.getQuantity() < 1) {
            throw new IllegalArgumentException(
                    "Số lượng thiết bị phải lớn hơn 0");
        }

        if (equipment.getStatus() == null
                || !VALID_STATUSES.contains(equipment.getStatus())) {
            throw new IllegalArgumentException(
                    "Trạng thái thiết bị không hợp lệ");
        }

        if (equipment.getRoom() == null
                || equipment.getRoom().getRoomId() == null) {
            throw new IllegalArgumentException(
                    "Vui lòng chọn phòng");
        }

        // Kiểm tra phòng có tồn tại không
        Integer roomId = equipment.getRoom().getRoomId();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy phòng có ID: " + roomId));

        equipment.setRoom(room);

        Equipment saved;

        if (equipment.getEquipmentId() == null) {
            // Thêm mới
            equipment.setCreatedAt(LocalDateTime.now());
            saved = equipmentRepository.save(equipment);

        } else {
            // Cập nhật thiết bị hiện có
            Equipment existing = equipmentRepository
                    .findById(equipment.getEquipmentId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy thiết bị có ID: "
                                    + equipment.getEquipmentId()));

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

    // Xóa thiết bị
    public void deleteEquipment(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thiết bị có ID: " + id));

        equipmentRepository.delete(equipment);
    }
}
