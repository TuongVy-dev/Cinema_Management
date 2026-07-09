package vn.edu.fpt.cinemamanagement.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.cinemamanagement.entities.Staff;
import java.util.Optional;
import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, String> {
    Staff findTopByOrderByStaffIDDesc();

    Optional<Staff> findByUsername(String username);
    Staff findByPhone(String phone);
    Staff findByEmail(String email);
    List<Staff> findAllByUsername(String username);

    // Dùng cho REST API: tìm kiếm theo fullName hoặc staffID
    Page<Staff> findByFullNameContainingIgnoreCaseOrStaffIDContainingIgnoreCase(
            String fullName, String staffID, Pageable pageable);
}