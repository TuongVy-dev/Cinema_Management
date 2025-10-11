package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class AdminController {
    @GetMapping
    public String adminHome() {
        return "dashboard/dashboard"; // đúng 100% với tên file
    }
}
