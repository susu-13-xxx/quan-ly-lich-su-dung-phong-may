package vn.edu.roomlab.dto;

public record IncidentRequest(
        Integer roomId,
        Integer equipmentId,
        String title,
        String description,
        String priority,
        String status,
        String equipmentType
) {
}
