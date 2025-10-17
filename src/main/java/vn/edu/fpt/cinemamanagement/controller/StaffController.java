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

    // --- List all staff---
    @GetMapping
    public String getAllStaff(Model model) {
        // add list of all staff to the model
        model.addAttribute("staffList", staffService.getAllStaff());
        return "staffs/staff_list";
    }

    // --- Create form ---
    @GetMapping("/create")
    public String createStaff(Model model) {
       //add an empty Staff object to the model to bind form data
        model.addAttribute("staff", new Staff());
        return "staffs/staff_create";
    }

    // --- Show Create form  ---
    @PostMapping("/save")
    public String saveStaff(@ModelAttribute Staff staff, Model model) {
        // Check validation before save: GỌI HÀM TỪ SERVICE
        boolean hasError = staffService.validateStaff(staff, model, false);
        // If validation fails, return to the create form with error messages
        if (hasError) {
            model.addAttribute("staff", staff);
            return "staffs/staff_create"; // return  form create
        }
// If validation passes, save the new staff to the database
        staffService.createStaff(staff);
        // Redirect to the staff list page
        return "redirect:/staffs";
    }
    // --- Show Update Staff Form ---
    @GetMapping("/update/{id}")
    public String updateStaffForm(@PathVariable("id") String staffID, Model model) {
        // Get the existing staff by ID
        Staff staff = staffService.getStaffByID(staffID);
        if (staff == null) {
            return "redirect:/staffs";
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

        // Giữ lại ID, phone, email, password cũ
        staff.setStaffID(existing.getStaffID());
        staff.setPhone(existing.getPhone());
        staff.setEmail(existing.getEmail());
        staff.setPassword(existing.getPassword());

        boolean hasError = staffService.validateStaff(staff, model, true);
        if (hasError) {
            model.addAttribute("staff", staff);
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
