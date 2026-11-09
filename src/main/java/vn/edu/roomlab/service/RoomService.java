package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.Room;
import vn.edu.roomlab.repository.RoomRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RoomService {

    private static final Set<String> VALID_STATUSES =
            Set.of("AVAILABLE", "MAINTENANCE");

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy phòng có ID: " + id));
    }

    public Room saveRoom(Room room) {
        if (room.getRoomName() == null || room.getRoomName().isBlank()) {
            throw new IllegalArgumentException("Tên phòng không được để trống");
        }
        if (room.getCapacity() == null || room.getCapacity() < 1) {
            throw new IllegalArgumentException("Sức chứa phải lớn hơn 0");
        }
        if (room.getStatus() == null || room.getStatus().isBlank()) {
            room.setStatus("AVAILABLE");
        }
        if (!VALID_STATUSES.contains(room.getStatus())) {
            throw new IllegalArgumentException("Trạng thái phòng không hợp lệ");
        }
        if (room.getRoomId() == null && room.getCreatedAt() == null) {
            room.setCreatedAt(LocalDateTime.now());
        }
        return roomRepository.save(room);
    }

    public void updateRoomStatus(Integer id, String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Trạng thái phòng không hợp lệ");
        }
        Room room = getRoomById(id);
        room.setStatus(status);
        roomRepository.save(room);
    }

    public void deleteRoom(Integer id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }
}
