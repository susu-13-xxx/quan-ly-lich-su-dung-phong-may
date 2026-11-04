package vn.edu.roomlab.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import vn.edu.roomlab.dto.IncidentRequest;
import vn.edu.roomlab.dto.IncidentResponse;
import vn.edu.roomlab.service.IncidentService;

@RestController
@RequestMapping("/api/incidents")
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
    public IncidentResponse create(
            @RequestBody IncidentRequest request,
            Authentication authentication
    ) {
        return incidentService.createIncident(
                request,
                authentication.getName()
        );
    }

    @PutMapping("/{id}")
    public IncidentResponse update(
            @PathVariable Integer id,
            @RequestBody IncidentRequest request
    ) {
        return incidentService.updateIncident(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        incidentService.deleteIncident(id);
    }
}