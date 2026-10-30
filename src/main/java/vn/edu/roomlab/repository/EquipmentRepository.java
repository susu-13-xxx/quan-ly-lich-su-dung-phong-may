package vn.edu.roomlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.roomlab.entity.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
}