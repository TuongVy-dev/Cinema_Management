package vn.edu.fpt.cinemamanagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model; // Cần import Model
import vn.edu.fpt.cinemamanagement.entities.Staff;
import vn.edu.fpt.cinemamanagement.repositories.StaffRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepo;

    // --- Read All ---
    public List<Staff> getAllStaff() {
        return staffRepo.findAll();
    }

    // --- Read One ---
    public Staff getStaffByID(String staffID) {
        Optional<Staff> optionalStaff = staffRepo.findById(staffID);
        return optionalStaff.orElse(null);
    }

    // --- Create / Save ---
    public void createStaff(Staff staff) {
        if (staff.getStaffID() == null || staff.getStaffID().isEmpty()) {
            staff.setStaffID(generateNewStaffID());
        }
        staffRepo.save(staff);
    }

    // --- Update ---
    public void updateStaff(Staff staff) {
        if (staff.getStaffID() != null && staffRepo.existsById(staff.getStaffID())) {
            Staff existing = staffRepo.findById(staff.getStaffID()).get();

            // If the password input contains only * characters, keep the old password.
            if (staff.getPassword() != null && staff.getPassword().equals("*".repeat(existing.getPassword().length()))) {
                staff.setPassword(existing.getPassword());
            }

            staffRepo.save(staff);
        }
    }


    // --- Delete ---
    public void deleteStaffByID(String staffID) {
        if (staffRepo.existsById(staffID)) {
            staffRepo.deleteById(staffID);
        }
    }

    // --- ID Generator ---
    private String generateNewStaffID() {
        Staff lastStaff = staffRepo.findTopByOrderByStaffIDDesc();
        if (lastStaff == null) {
            return "ST0001";
        }
        String lastID = lastStaff.getStaffID(); // Ex: ST0009
        int number = Integer.parseInt(lastID.substring(2)) + 1;
        return String.format("ST%04d", number); // -> ST0010
    }


    // ------Validate--------
    public boolean validateStaff(Staff staff, Model model, boolean isUpdate) {
        boolean hasError = false;

        // --- Password ----
        boolean shouldValidatePassword = !isUpdate ||
                (staff.getStaffID() != null && getStaffByID(staff.getStaffID()) != null && // Dùng getStaffByID() của Service
                        !staff.getPassword().equals("*".repeat(getStaffByID(staff.getStaffID()).getPassword().length())));

        if (shouldValidatePassword) {
            if (staff.getPassword() == null || staff.getPassword().isEmpty() ||
                    !staff.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$")) {
                model.addAttribute("errorPassword", "Password must be ≥6 chars with upper, lower, number & symbol");
                hasError = true;
            }
        }

        // ---  Phone ---
        if (staff.getPhone() == null || staff.getPhone().isEmpty() ||
                !staff.getPhone().matches("^0\\d{9}$")) {
            model.addAttribute("errorPhone", "Phone must be 10 digits starting with 0");
            hasError = true;
        }

        // --- Email ---
        if (staff.getEmail() == null || staff.getEmail().isEmpty() ||
                !staff.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            model.addAttribute("errorEmail", "Invalid email format");
            hasError = true;
        }
        return hasError;
    }
}
