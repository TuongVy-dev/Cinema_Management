package vn.edu.fpt.cinemamanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.cinemamanagement.entities.Staff;

public interface StaffRepository extends JpaRepository<Staff, String> {
    Staff findTopByOrderByStaffIDDesc();
}