package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {


    @GetMapping("")
    public String dashboard(){
    return "dashboard/dashboard";
    }

    @GetMapping("/seat")
    public String seat(Model model) {
        return "seats/seat";
    }

}
