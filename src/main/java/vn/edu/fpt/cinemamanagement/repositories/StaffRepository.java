package vn.edu.fpt.cinemamanagement.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.cinemamanagement.entities.Staff;
import java.util.Optional;
import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, String> {
    Staff findTopByOrderByStaffIDDesc();

    Optional<Staff> findByUsername(String username);

    Optional<Staff> findByStaffID(String staffID);
    Staff findByPhone(String phone);
    Staff findByEmail(String email);
    List<Staff> findAllByUsername(String username);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
    UPDATE Staff s
    SET s.password = :password
    WHERE s.staffID = :id
    """)
    int updateResetPassword(@Param("id") String id,
                            @Param("password") String password);

    // Dùng cho REST API: tìm kiếm theo fullName hoặc staffID
    Page<Staff> findByFullNameContainingIgnoreCaseOrStaffIDContainingIgnoreCase(
            String fullName, String staffID, Pageable pageable);
}