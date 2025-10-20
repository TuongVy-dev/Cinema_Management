package vn.edu.fpt.cinemamanagement.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping("")
    public String dashboard(HttpServletRequest request) {
        // ===== LẤY ROLE TỪ COOKIE =====
        String role = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("user_role".equals(cookie.getName())) {
                    role = cookie.getValue();
                    break;
                }
            }
        }

        // ===== KIỂM TRA ROLE =====
        if (role == null || !role.toLowerCase().contains("admin")) {
            // Nếu không có cookie hoặc không phải admin -> chặn truy cập
            return "redirect:/access-denied";
        }

        // Nếu là admin thì cho vào dashboard
        return "dashboard/dashboard";
    }

    @GetMapping("/seat")
    public String seat(HttpServletRequest request, Model model) {
        // ===== LẤY ROLE TỪ COOKIE =====
        String role = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("user_role".equals(cookie.getName())) {
                    role = cookie.getValue();
                    break;
                }
            }
        }

        // ===== KIỂM TRA ROLE =====
        if (role == null || !role.toLowerCase().contains("admin")) {
            return "redirect:/access-denied";
        }

        // Nếu là admin -> cho truy cập trang seat
        return "seats/seat";
    }
}
