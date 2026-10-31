package vn.edu.roomlab.dto;

import java.time.LocalDateTime;
import vn.edu.roomlab.entity.Equipment;

public record EquipmentResponse(
        Integer equipmentId,
        String equipmentName,
        String equipmentType,
        Integer roomId,
        Integer quantity,
        String status,
        String description,
        LocalDateTime createdAt
) {
    public static EquipmentResponse from(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getEquipmentId(),
                equipment.getEquipmentName(),
                equipment.getEquipmentType(),
                equipment.getRoom() != null
                        ? equipment.getRoom().getRoomId() : null,
                equipment.getQuantity(),
                equipment.getStatus(),
                equipment.getDescription(),
                equipment.getCreatedAt()
        );
    }
}