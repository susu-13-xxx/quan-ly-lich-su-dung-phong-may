package vn.edu.roomlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.roomlab.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}