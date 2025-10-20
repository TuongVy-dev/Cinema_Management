package vn.edu.fpt.cinemamanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.entities.Customer;
import vn.edu.fpt.cinemamanagement.entities.Staff;
import vn.edu.fpt.cinemamanagement.services.AuthService;
import vn.edu.fpt.cinemamanagement.services.CustomerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class AuthController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AuthService authService;

    // ========================== LOGIN ==========================

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletResponse response,
            Model model
    ) {
        // ===== VALIDATION =====
        if (username == null || username.isBlank()) {
            model.addAttribute("error", "Username cannot be empty!");
            return "auth/login";
        }

        if (password == null || password.isBlank()) {
            model.addAttribute("error", "Password cannot be empty!");
            return "auth/login";
        }

        // ===== STAFF / ADMIN LOGIN =====
        Staff staff = authService.checkStaffLogin(username, password);
        if (staff != null) {
            String safeRole = URLEncoder.encode(staff.getPosition(), StandardCharsets.UTF_8); // ✅ encode role để tránh lỗi space

            Cookie usernameCookie = new Cookie("user_name", username);
            Cookie roleCookie = new Cookie("user_role", safeRole);
            roleCookie.setPath("/");
            usernameCookie.setMaxAge(2 * 60 * 60);
            response.addCookie(usernameCookie);
            roleCookie.setPath("/");
            roleCookie.setMaxAge(2 * 60 * 60);
            response.addCookie(roleCookie);

            String role = staff.getPosition().toLowerCase();
            if (role.contains("admin")) {
                return "redirect:/homepage";
            } else if (role.contains("cashier")) {
                return "redirect:/homepage";
            } else if (role.contains("redemption")) {
                return "redirect:/homepage";
            }
        }

        // ===== CUSTOMER LOGIN =====
        Customer customer = authService.checkCustomerLogin(username, password);
        if (customer != null) {
            Cookie usernameCookie = new Cookie("user_name", username);
            Cookie roleCookie = new Cookie("user_role", "customer");
            roleCookie.setPath("/");
            usernameCookie.setMaxAge(30 * 60);
            response.addCookie(usernameCookie);
            roleCookie.setPath("/");
            roleCookie.setMaxAge(30 * 60);
            response.addCookie(roleCookie);
            return "redirect:/homepage";
        }

        model.addAttribute("error", "Invalid username or password!");
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie roleCookie = new Cookie("user_role", "");
        roleCookie.setPath("/");
        roleCookie.setMaxAge(0);
        response.addCookie(roleCookie);

        Cookie nameCookie = new Cookie("user_name", "");
        nameCookie.setPath("/");
        nameCookie.setMaxAge(0);
        response.addCookie(nameCookie);

        return "redirect:/login";
    }
    // ========================== REGISTER FORM (GET) ==========================
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("pageTitle", "Register");
        model.addAttribute("customer", new Customer());
        return "auth/register"; // trỏ đến templates/auth/register.html
    }
    @PostMapping(value = "/register")
    public String register(@ModelAttribute Customer customer,
                           @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                           @RequestParam(value = "agreedToTerms", required = false, defaultValue = "false") Boolean agreedToTerms,
                           Model model) {
        // Kiểm tra đồng ý điều khoản
        if (!agreedToTerms) {
            model.addAttribute("errors", Map.of("terms", "You must agree to the terms of service."));
            model.addAttribute("customer", customer);
            return "auth/register";
        }
        // Gọi service để validate và đăng ký
        Map<String, String> errors = customerService.registerCustomer(customer, confirmPassword);

        if (!errors.isEmpty()) {
            // Có lỗi - trả về form với thông báo lỗi
            model.addAttribute("errors", errors);
            model.addAttribute("customer", customer);
            return "auth/register";
        }

        // Thành công - redirect về homepage
        return "redirect:/homepage";
    }
}
