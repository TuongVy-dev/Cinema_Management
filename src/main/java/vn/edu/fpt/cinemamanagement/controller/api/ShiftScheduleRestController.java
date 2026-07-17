package vn.edu.fpt.cinemamanagement.controller.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.dto.request.ShiftScheduleRequestDTO;
import vn.edu.fpt.cinemamanagement.entities.ShiftSchedule;
import vn.edu.fpt.cinemamanagement.entities.Staff;
import vn.edu.fpt.cinemamanagement.services.ShiftScheduleService;
import vn.edu.fpt.cinemamanagement.services.StaffService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174", "http://127.0.0.1:5173"}, allowCredentials = "true")
@RequestMapping("/api/staff-schedules")
public class ShiftScheduleRestController {

    private final ShiftScheduleService shiftScheduleService;
    private final StaffService staffService;

    public ShiftScheduleRestController(ShiftScheduleService shiftScheduleService, StaffService staffService) {
        this.shiftScheduleService = shiftScheduleService;
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<?> getAllSchedules(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        int pageIndex = Math.max(page, 1) - 1;
        int pageSize = Math.max(size, 1);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        Page<ShiftSchedule> schedulePage;

        if (date != null) {
            schedulePage = shiftScheduleService.findByShiftDate(date, pageable);
        } else {
            schedulePage = shiftScheduleService.findAllSchedules(pageable);
        }

        Map<String, Object> response = Map.of(
                "scheduleList", schedulePage.getContent(),
                "currentPage", page,
                "totalPages", schedulePage.getTotalPages(),
                "totalItems", schedulePage.getTotalElements(),
                "pageSize", pageSize
        );

        return ResponseEntity.ok(response);
    }



    @GetMapping("/cashier")
    public ResponseEntity<?> getCashierSchedules(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "date", required = false) java.time.LocalDate date) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String username = authentication.getName();
        Staff currentStaff = staffService.findByAccountUsername(username);
        System.out.println("===== CURRENT STAFF =====");
        System.out.println(currentStaff.getStaffID());
        System.out.println(currentStaff.getFullName());
        System.out.println(currentStaff.getUsername());
        List<ShiftSchedule> all = shiftScheduleService.findByShiftDate(date);

        System.out.println("===== ALL SHIFT OF DAY =====");

        for (ShiftSchedule s : all) {
            System.out.println(
                    s.getShiftScheduleId()
                            + " | "
                            + s.getStaff().getStaffID()
                            + " | "
                            + s.getShiftDate()
            );
        }
        if (currentStaff == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Staff not found"));
        }

        if (date == null) {
            date = java.time.LocalDate.now();
        }

        int pageIndex = Math.max(page, 1) - 1;
        int pageSize = Math.max(size, 1);

        var pageable = org.springframework.data.domain.PageRequest.of(pageIndex, pageSize);
        var schedulePage = shiftScheduleService.findByStaffAndShiftDate(currentStaff, date, pageable);
        System.out.println("Total = " + schedulePage.getTotalElements());
        System.out.println("Size = " + schedulePage.getContent().size());

        for (ShiftSchedule s : schedulePage.getContent()) {
            System.out.println("FOUND -> " + s.getShiftScheduleId());
        }
        Map<String, Object> response = Map.of(
                "scheduleList", schedulePage.getContent(),
                "currentPage", page,
                "totalPages", schedulePage.getTotalPages(),
                "totalItems", schedulePage.getTotalElements(),
                "pageSize", pageSize,
                "currentDate", date
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable String id) {
        Optional<ShiftSchedule> optional = shiftScheduleService.getShiftScheduleById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(optional.get());
    }

    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody ShiftScheduleRequestDTO dto) {
        try {
            Staff staff = staffService.getStaffByID(dto.getStaffId());
            if (staff == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Staff not found"));
            }

            ShiftSchedule shiftSchedule = new ShiftSchedule();
            shiftSchedule.setShiftScheduleId(dto.getShiftScheduleId());
            shiftSchedule.setStaff(staff);
            shiftSchedule.setShiftDate(dto.getShiftDate());
            shiftSchedule.setStatus(dto.getStatus());
            shiftSchedule.setShiftStart(dto.getShiftStart());
            shiftSchedule.setLateMinutes(dto.getLateMinutes());

            ShiftSchedule saved = shiftScheduleService.save(shiftSchedule);
            return ResponseEntity.ok(Map.of(
                    "message", "Shift schedule created successfully",
                    "shiftScheduleId", saved.getShiftScheduleId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable String id, @RequestBody ShiftScheduleRequestDTO dto) {
        try {
            Optional<ShiftSchedule> optional = shiftScheduleService.getShiftScheduleById(id);
            if (optional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Staff staff = staffService.getStaffByID(dto.getStaffId());
            if (staff == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Staff not found"));
            }

            ShiftSchedule existing = optional.get();
            existing.setStaff(staff);
            existing.setShiftDate(dto.getShiftDate());
            existing.setStatus(dto.getStatus());
            existing.setShiftStart(dto.getShiftStart());
            existing.setLateMinutes(dto.getLateMinutes());

            ShiftSchedule saved = shiftScheduleService.save(existing);
            return ResponseEntity.ok(Map.of(
                    "message", "Shift schedule updated successfully",
                    "shiftScheduleId", saved.getShiftScheduleId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable String id) {
        try {
            shiftScheduleService.deleteShiftSchedule(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Shift schedule deleted successfully",
                    "shiftScheduleId", id
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<?> checkIn(@PathVariable String id) {

        try {

            ShiftSchedule shift = shiftScheduleService.checkInShift(id);

            return ResponseEntity.ok(shift);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }
}
