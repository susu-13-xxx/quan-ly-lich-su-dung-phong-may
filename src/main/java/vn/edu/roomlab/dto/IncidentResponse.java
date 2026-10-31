package vn.edu.roomlab.dto;

import java.time.LocalDateTime;
import vn.edu.roomlab.entity.Incident;

public record IncidentResponse(
        Integer incidentId,
        Integer userId,
        Integer roomId,
        Integer equipmentId,
        String title,
        String description,
        String priority,
        String status,
        LocalDateTime reportedAt,
        LocalDateTime resolvedAt
) {
    public static IncidentResponse from(Incident incident) {
        return new IncidentResponse(
                incident.getIncidentId(),
                incident.getUser() != null
                        ? incident.getUser().getUserId() : null,
                incident.getRoom() != null
                        ? incident.getRoom().getRoomId() : null,
                incident.getEquipment() != null
                        ? incident.getEquipment().getEquipmentId() : null,
                incident.getTitle(),
                incident.getDescription(),
                incident.getPriority(),
                incident.getStatus(),
                incident.getReportedAt(),
                incident.getResolvedAt()
        );
    }
}