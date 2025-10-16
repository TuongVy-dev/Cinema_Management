package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.entities.Staff;
import vn.edu.fpt.cinemamanagement.services.StaffService;

@Controller
@RequestMapping("/staffs")
public class StaffController {

    @Autowired
    private StaffService staffService;

    // --- List ---
    @GetMapping
    public String getAllStaff(Model model) {
        model.addAttribute("staffList", staffService.getAllStaff());
        return "staffs/staff_list";
    }

    // --- Create form ---
    @GetMapping("/create")
    public String createStaff(Model model) {
        model.addAttribute("staff", new Staff());
        return "staffs/staff_create";
    }

    // --- Create Save ---
    @PostMapping("/save")
    public String saveStaff(@ModelAttribute Staff staff, Model model) {
        // Check validation before save: GỌI HÀM TỪ SERVICE
        boolean hasError = staffService.validateStaff(staff, model, false);
        if (hasError) {
            model.addAttribute("staff", staff);
            return "staffs/staff_create"; // return  form create
        }

        staffService.createStaff(staff);
        return "redirect:/staffs";
    }

    // --- Update form ---
    @GetMapping("/update/{id}")
    public String updateStaffForm(@PathVariable("id") String staffID, Model model) {
        Staff staff = staffService.getStaffByID(staffID);
        if (staff == null) {
            return "redirect:/staffs";
        }

        // 1. DISPLAY: Mask the password with a string of ***** of the same length
        if (staff.getPassword() != null && !staff.getPassword().isEmpty()) {
            staff.setPassword("*".repeat(staff.getPassword().length()));
        }

        model.addAttribute("staff", staff);
        return "staffs/staff_update";
    }

    // --- Update Save ---
    @PostMapping("/update/{id}")
    public String updateStaff(@PathVariable("id") String staffID, @ModelAttribute Staff staff, Model model) {
        Staff existing = staffService.getStaffByID(staffID);
        if (existing == null) {
            return "redirect:/staffs";
        }

        // Ensure the staffID is correct
        staff.setStaffID(staffID);

        // Handle password logic before validation
        int originalPasswordLength = existing.getPassword() != null ? existing.getPassword().length() : 0;
        String obscuredPassword = "*".repeat(originalPasswordLength);

        // If the entered password is a string of *****, restore the old password
        if (staff.getPassword() != null && staff.getPassword().equals(obscuredPassword)) {
            staff.setPassword(existing.getPassword());
        }

        // Validate: GỌI HÀM TỪ SERVICE
        boolean hasError = staffService.validateStaff(staff, model, true);

        if (hasError) {
            // If validation fails, reset the password field to ***** before rendering the view again
            staff.setPassword(obscuredPassword);

            model.addAttribute("staff", staff);
            // return view update
            return "staffs/staff_update";
        }

        staffService.updateStaff(staff);

        return "redirect:/staffs";
    }

    // --- Delete ---
    @PostMapping("/delete/{id}")
    public String deleteStaff(@PathVariable("id") String staffID) {
        staffService.deleteStaffByID(staffID);
        return "redirect:/staffs";
    }

    // --- Detail ---
    @GetMapping("/detail/{id}")
    public String staffDetail(@PathVariable("id") String staffID, Model model) {
        Staff staff = staffService.getStaffByID(staffID);
        if (staff == null) {
            return "redirect:/staffs";
        }
        model.addAttribute("staff", staff);
        return "staffs/staff_detail";
    }

}
