package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.Incident;
import vn.edu.roomlab.repository.IncidentRepository;
import vn.edu.roomlab.dto.IncidentResponse;

import java.util.List;

@Service
@Transactional
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public List<IncidentResponse> getAllIncidents() {
        return incidentRepository.findAll()
                .stream()
                .map(IncidentResponse::from)
                .toList();
    }

    public IncidentResponse getIncidentById(Integer id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy sự cố có ID: " + id));

        return IncidentResponse.from(incident);
    }

    public IncidentResponse saveIncident(Incident incident) {
        return IncidentResponse.from(
                incidentRepository.save(incident)
        );
    }

    public void deleteIncident(Integer id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy sự cố có ID: " + id));

        incidentRepository.delete(incident);
    }
}