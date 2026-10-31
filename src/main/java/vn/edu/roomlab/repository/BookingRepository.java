package vn.edu.roomlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.roomlab.entity.Booking;

public interface BookingRepository
        extends JpaRepository<Booking, Integer> {
}