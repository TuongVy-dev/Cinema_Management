package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Dashboard")
public class AdminController {
    @GetMapping
    public String adminHome() {
        return "Dashboard/Dashboard"; // đúng 100% với tên file
    }
}
