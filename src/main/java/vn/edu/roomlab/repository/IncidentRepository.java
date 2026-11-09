package vn.edu.roomlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.roomlab.entity.Incident;

import java.util.Collection;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    List<Incident> findByStatusIn(Collection<String> statuses);
}
