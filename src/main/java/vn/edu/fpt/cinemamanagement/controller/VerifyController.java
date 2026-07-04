package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.entities.Customer;
import vn.edu.fpt.cinemamanagement.services.CustomerService;
import vn.edu.fpt.cinemamanagement.services.ResetPasswordTokenService;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/verify")
public class VerifyController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ResetPasswordTokenService resetPasswordTokenService;

    @GetMapping("/done/{id}/{timestamp}/{code}")
    public String veryPage(@PathVariable(value="id") String id,
                           @PathVariable(value="timestamp") long timestamp,
                           @PathVariable(value="code") String code,
                           Model model){
        Customer customer = customerService.findCustomerById(id);
        if (customer == null) {
            model.addAttribute("error", "The URL is not valid");
            return "error/error500";
        }

        if (!"resetPassword".equals(customer.getVerify())) {
            model.addAttribute("error", "This reset link is no longer valid!");
            return "error/error500";
        }


            LocalDateTime requestTime = customer.getResetRequestedAt();
            LocalDateTime now = LocalDateTime.now();

            if (requestTime == null || !resetPasswordTokenService.matchesStoredTimestamp(requestTime, timestamp)) {
                model.addAttribute("error", "This reset link is no longer valid!");
                return "error/error500";
            }

            if(requestTime != null && now.isAfter(requestTime.plusMinutes(10))) {
                customer.setVerify("active");
                customer.setResetRequestedAt(null);
                customerService.save(customer);
                model.addAttribute("error", "Link expired, please request again!");
                return "error/error500";
            }

            if(resetPasswordTokenService.isValid(id, timestamp, code)){
                model.addAttribute("customer", customer);
                model.addAttribute("resetTimestamp", timestamp);
                model.addAttribute("resetCode", code);
                return "auth/reset_password";
            }


        model.addAttribute("error", "The URL is not valid");
        return "error/error500";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("id") String id,
                                @RequestParam("resetTimestamp") long resetTimestamp,
                                @RequestParam("resetCode") String resetCode,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {
        Customer customer = customerService.findCustomerById(id);
        if (customer == null
                || !"resetPassword".equals(customer.getVerify())
                || customer.getResetRequestedAt() == null
                || !resetPasswordTokenService.matchesStoredTimestamp(customer.getResetRequestedAt(), resetTimestamp)
                || !resetPasswordTokenService.isValid(id, resetTimestamp, resetCode)
                || LocalDateTime.now().isAfter(customer.getResetRequestedAt().plusMinutes(10))) {
            model.addAttribute("error", "This reset link is no longer valid!");
            return "error/error500";
        }

        Map<String, String> errors = customerService.resetPassword(id, newPassword, confirmPassword);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("customer", customer);
            model.addAttribute("resetTimestamp", resetTimestamp);
            model.addAttribute("resetCode", resetCode);
            return "auth/reset_password";
        }

        model.addAttribute("success", "Password changed successfully!");

        return "auth/login";

    }

}
