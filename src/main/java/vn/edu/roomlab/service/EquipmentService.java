package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.Equipment;
import vn.edu.roomlab.repository.EquipmentRepository;

import java.util.List;

@Service
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    public Equipment getEquipmentById(Integer id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thiết bị có ID: " + id));
    }

    public Equipment saveEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public void deleteEquipment(Integer id) {
        Equipment equipment = getEquipmentById(id);
        equipmentRepository.delete(equipment);
    }
}