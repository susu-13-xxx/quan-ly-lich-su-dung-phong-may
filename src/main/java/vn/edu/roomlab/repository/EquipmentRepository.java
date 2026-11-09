package vn.edu.roomlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.roomlab.entity.Equipment;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {

    List<Equipment> findByStatus(String status);

    List<Equipment> findByRoom_RoomIdAndStatus(Integer roomId, String status);
}
