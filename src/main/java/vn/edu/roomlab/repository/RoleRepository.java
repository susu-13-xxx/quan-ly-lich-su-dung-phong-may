package vn.edu.roomlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.roomlab.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}