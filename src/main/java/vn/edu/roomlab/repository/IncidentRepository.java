package vn.edu.roomlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.roomlab.entity.Incident;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {
}