package vn.edu.fpt.cinemamanagement.controller.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.entities.Staff;
import vn.edu.fpt.cinemamanagement.repositories.StaffRepository;
import vn.edu.fpt.cinemamanagement.services.StaffService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/staffs")
public class StaffRestController {

    private final StaffService staffService;
    private final StaffRepository staffRepository;

    public StaffRestController(StaffService staffService, StaffRepository staffRepository) {
        this.staffService = staffService;
        this.staffRepository = staffRepository;
    }

    // ========================== LIST (phân trang + search) ==========================
    @GetMapping
    public ResponseEntity<?> listStaffs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Staff> staffPage;

        if (search != null && !search.trim().isEmpty()) {
            // Tìm kiếm theo fullName hoặc staffID (không phân biệt hoa thường)
            staffPage = staffRepository.findByFullNameContainingIgnoreCaseOrStaffIDContainingIgnoreCase(
                    search.trim(), search.trim(), pageable);
        } else {
            staffPage = staffService.findAllStaff(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", staffPage.getContent());
        response.put("pageNumber", staffPage.getNumber());
        response.put("size", staffPage.getSize());
        response.put("totalElements", staffPage.getTotalElements());
        response.put("totalPages", staffPage.getTotalPages());
        response.put("last", staffPage.isLast());

        return ResponseEntity.ok(response);
    }

    // ========================== GET DETAIL ==========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getStaff(@PathVariable String id) {
        Staff staff = staffService.getStaffByID(id);
        if (staff == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Staff ID " + id + " does not exist."));
        }
        return ResponseEntity.ok(staff);
    }

    // ========================== GET NEXT ID ==========================
    @GetMapping("/next-id")
    public ResponseEntity<?> getNextId() {
        Staff lastStaff = staffRepository.findTopByOrderByStaffIDDesc();
        String nextId;
        if (lastStaff == null) {
            nextId = "ST000001";
        } else {
            String lastId = lastStaff.getStaffID();
            int number = Integer.parseInt(lastId.substring(2)) + 1;
            nextId = String.format("ST%06d", number);
        }
        return ResponseEntity.ok(Map.of("nextId", nextId));
    }

    // ========================== CREATE ==========================
    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody Staff staff) {
        Map<String, String> errors = validateStaffApi(staff, false);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        staffService.createStaff(staff);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Staff created successfully."));
    }

    // ========================== UPDATE ==========================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable String id, @RequestBody Staff staff) {
        Staff existing = staffService.getStaffByID(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Staff ID " + id + " does not exist."));
        }

        // Giữ lại staffID, phone, email, password gốc cho bước validate
        staff.setStaffID(existing.getStaffID());

        // Nếu password rỗng → giữ nguyên password cũ (không đổi)
        if (staff.getPassword() == null || staff.getPassword().trim().isEmpty()) {
            staff.setPassword(existing.getPassword());
            // Đánh dấu không cần validate password
            Map<String, String> errors = validateStaffApi(staff, true);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }
            // Giữ nguyên password hash cũ, cập nhật các field khác
            staffRepository.save(staff);
        } else {
            Map<String, String> errors = validateStaffApi(staff, false);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }
            // Encode password mới
            staff.setPassword(staffService.encodePassword(staff.getPassword()));
            staffRepository.save(staff);
        }

        return ResponseEntity.ok(Map.of("message", "Staff updated successfully."));
    }

    // ========================== DELETE ==========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable String id) {
        Staff existing = staffService.getStaffByID(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Staff ID " + id + " does not exist."));
        }
        staffService.deleteStaffByID(id);
        return ResponseEntity.ok(Map.of("message", "Staff deleted successfully."));
    }

    // ========================== VALIDATE (dành riêng cho REST, trả Map thay vì Model) ==========================
    private Map<String, String> validateStaffApi(Staff staff, boolean skipPassword) {
        Map<String, String> errors = new HashMap<>();

        // --- Username ---
        if (staff.getUsername() == null || staff.getUsername().isEmpty() ||
                !staff.getUsername().matches("^(?=.*[a-z])(?=.*[0-9])[a-z0-9]+$")) {
            errors.put("username", "Username must contain both lowercase letters and numbers");
        } else {
            List<Staff> sameUsername = staffRepository.findAllByUsername(staff.getUsername());
            for (Staff s : sameUsername) {
                if (!s.getStaffID().equals(staff.getStaffID())) {
                    errors.put("username", "Username already exists");
                    break;
                }
            }
        }

        // --- Full Name ---
        if (staff.getFullName() == null || staff.getFullName().trim().isEmpty()) {
            errors.put("fullName", "Full Name cannot be empty");
        } else {
            String normalized = staff.getFullName().trim().replaceAll("\\s+", " ");
            String[] words = normalized.split(" ");
            if (words.length < 2) {
                errors.put("fullName", "Full Name must contain at least 2 words");
            } else {
                boolean valid = true;
                for (String word : words) {
                    if (!word.matches("^[A-Za-zÀ-ỹ]+$")) {
                        valid = false;
                        break;
                    }
                }
                if (!valid) {
                    errors.put("fullName", "Full Name cannot contain numbers or special characters");
                } else {
                    // Chuẩn hóa viết hoa chữ đầu
                    StringBuilder sb = new StringBuilder();
                    for (String word : words) {
                        sb.append(word.substring(0, 1).toUpperCase())
                          .append(word.substring(1).toLowerCase())
                          .append(" ");
                    }
                    staff.setFullName(sb.toString().trim());
                }
            }
        }

        // --- Password (chỉ validate khi không skipPassword) ---
        if (!skipPassword) {
            if (staff.getPassword() == null || staff.getPassword().isEmpty() ||
                    !staff.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$")) {
                errors.put("password", "Password must be ≥6 chars with upper, lower, number & symbol");
            }
        }

        // --- Phone ---
        if (staff.getPhone() == null || staff.getPhone().isEmpty()) {
            errors.put("phone", "Phone cannot be empty");
        } else if (!staff.getPhone().matches("^0\\d{9}$")) {
            errors.put("phone", "Phone must be 10 digits starting with 0");
        } else {
            Staff existingByPhone = staffRepository.findByPhone(staff.getPhone());
            if (existingByPhone != null && !existingByPhone.getStaffID().equals(staff.getStaffID())) {
                errors.put("phone", "Phone already exists");
            }
        }

        // --- Email ---
        if (staff.getEmail() == null || staff.getEmail().isEmpty()) {
            errors.put("email", "Email cannot be empty");
        } else {
            staff.setEmail(staff.getEmail().toLowerCase());
            if (!staff.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                errors.put("email", "Invalid email format");
            } else {
                Staff existingByEmail = staffRepository.findByEmail(staff.getEmail());
                if (existingByEmail != null && !existingByEmail.getStaffID().equals(staff.getStaffID())) {
                    errors.put("email", "Email already exists");
                }
            }
        }

        // --- Position ---
        if (staff.getPosition() == null || staff.getPosition().trim().isEmpty()) {
            errors.put("position", "Position cannot be empty");
        }

        return errors;
    }
}
