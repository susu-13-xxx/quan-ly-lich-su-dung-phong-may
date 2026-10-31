package vn.edu.roomlab.controller;

import org.springframework.web.bind.annotation.*;

import vn.edu.roomlab.entity.Equipment;
import vn.edu.roomlab.service.EquipmentService;
import vn.edu.roomlab.dto.EquipmentResponse;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<EquipmentResponse> getAll() {
        return equipmentService.getAllEquipments();
    }

    @GetMapping("/{id}")
    public EquipmentResponse getById(@PathVariable Integer id) {
        return equipmentService.getEquipmentById(id);
    }

    @PostMapping
    public EquipmentResponse create(@RequestBody Equipment equipment) {
        return equipmentService.saveEquipment(equipment);
    }

    @PutMapping("/{id}")
    public EquipmentResponse update(
            @PathVariable Integer id,
            @RequestBody Equipment equipment) {
        equipment.setEquipmentId(id);
        return equipmentService.saveEquipment(equipment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        equipmentService.deleteEquipment(id);
    }
}