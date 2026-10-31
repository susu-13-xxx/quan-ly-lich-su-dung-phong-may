
package vn.edu.roomlab.controller;

import org.springframework.web.bind.annotation.*;
import vn.edu.roomlab.entity.Incident;
import vn.edu.roomlab.service.IncidentService;

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
    public List<Incident> getAll() {
        return incidentService.getAllIncidents();
    }

    @GetMapping("/{id}")
    public Incident getById(@PathVariable Integer id) {
        return incidentService.getIncidentById(id);
    }

    @PostMapping
    public Incident create(@RequestBody Incident incident) {
        return incidentService.saveIncident(incident);
    }

    @PutMapping("/{id}")
    public Incident update(@PathVariable Integer id,
                           @RequestBody Incident incident) {
        incident.setIncidentId(id);
        return incidentService.saveIncident(incident);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        incidentService.deleteIncident(id);
    }
}
