package vn.edu.roomlab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.roomlab.entity.Incident;
import vn.edu.roomlab.repository.IncidentRepository;

import java.util.List;

@Service
@Transactional
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public Incident getIncidentById(Integer id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy sự cố có ID: " + id));
    }

    public Incident saveIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    public void deleteIncident(Integer id) {
        Incident incident = getIncidentById(id);
        incidentRepository.delete(incident);
    }
}