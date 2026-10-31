package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.Equipment;
import vn.edu.roomlab.repository.EquipmentRepository;
import vn.edu.roomlab.dto.EquipmentResponse;

import java.util.List;

@Service
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<EquipmentResponse> getAllEquipments() {
        return equipmentRepository.findAll()
                .stream()
                .map(EquipmentResponse::from)
                .toList();
    }

    public EquipmentResponse getEquipmentById(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thiết bị có ID: " + id));

        return EquipmentResponse.from(equipment);
    }

    public EquipmentResponse saveEquipment(Equipment equipment) {
        Equipment saved = equipmentRepository.save(equipment);
        return EquipmentResponse.from(saved);
    }

    public void deleteEquipment(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thiết bị có ID: " + id));

        equipmentRepository.delete(equipment);
    }
}