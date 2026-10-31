
package vn.edu.roomlab.controller;

import org.springframework.web.bind.annotation.*;
import vn.edu.roomlab.entity.Equipment;
import vn.edu.roomlab.service.EquipmentService;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<Equipment> getAll() {
        return equipmentService.getAllEquipments();
    }

    @GetMapping("/{id}")
    public Equipment getById(@PathVariable Integer id) {
        return equipmentService.getEquipmentById(id);
    }

    @PostMapping
    public Equipment create(@RequestBody Equipment equipment) {
        return equipmentService.saveEquipment(equipment);
    }

    @PutMapping("/{id}")
    public Equipment update(@PathVariable Integer id,
                            @RequestBody Equipment equipment) {
        equipment.setEquipmentId(id);
        return equipmentService.saveEquipment(equipment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        equipmentService.deleteEquipment(id);
    }
}
