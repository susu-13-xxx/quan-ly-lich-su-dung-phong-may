package vn.edu.roomlab.controller;

import org.springframework.web.bind.annotation.*;

import vn.edu.roomlab.entity.Incident;
import vn.edu.roomlab.service.IncidentService;
import vn.edu.roomlab.dto.IncidentResponse;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = "*")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    public List<IncidentResponse> getAll() {
        return incidentService.getAllIncidents();
    }

    @GetMapping("/{id}")
    public IncidentResponse getById(@PathVariable Integer id) {
        return incidentService.getIncidentById(id);
    }

    @PostMapping
    public IncidentResponse create(@RequestBody Incident incident) {
        return incidentService.saveIncident(incident);
    }

    @PutMapping("/{id}")
    public IncidentResponse update(
            @PathVariable Integer id,
            @RequestBody Incident incident) {
        incident.setIncidentId(id);
        return incidentService.saveIncident(incident);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        incidentService.deleteIncident(id);
    }
}