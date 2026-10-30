package vn.edu.roomlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.roomlab.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}