package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.cinemamanagement.dto.ForgotPasswordRequestDTO;
import vn.edu.fpt.cinemamanagement.dto.ResetPasswordRequestDTO;
import vn.edu.fpt.cinemamanagement.entities.Customer;
import vn.edu.fpt.cinemamanagement.services.CustomerService;
import vn.edu.fpt.cinemamanagement.services.MailService;
import vn.edu.fpt.cinemamanagement.services.ResetPasswordTokenService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final CustomerService customerService;
    private final MailService mailService;
    private final ResetPasswordTokenService resetPasswordTokenService;

    public AuthApiController(CustomerService customerService,
                             MailService mailService,
                             ResetPasswordTokenService resetPasswordTokenService) {
        this.customerService = customerService;
        this.mailService = mailService;
        this.resetPasswordTokenService = resetPasswordTokenService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        if (request.email() == null || request.email().isBlank()) {
            return validationError("email", "Email is required.");
        }

        Customer customer = customerService.findCustomerByEmail(request.email());
        if (customer == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", Map.of(
                            "code", "EMAIL_NOT_FOUND",
                            "message", "No account found for this email."
                    )
            ));
        }

        long resetTimestamp = resetPasswordTokenService.currentTimestamp();
        customer.setVerify("resetPassword");
        customer.setResetRequestedAt(resetPasswordTokenService.toLocalDateTime(resetTimestamp));

        String token = resetPasswordTokenService.createToken(customer.getUser_id(), resetTimestamp);
        String link = "http://localhost:5173/reset-password/" + customer.getUser_id() + "/" + resetTimestamp + "/" + token;
        String content =
                "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                        "<h2>Hello " + customer.getUsername() + ",</h2>" +
                        "<p>We received a request to reset your CGV Movies account password.</p>" +
                        "<p>To set a new password, please click the button below:</p>" +
                        "<p><a href='" + link + "' style='background-color:#9D1212;color:white;padding:10px 20px;text-decoration:none;border-radius:6px;display:inline-block;'>Reset Password</a></p>" +
                        "<p>This link will expire soon for your account's security.</p>" +
                        "<hr><p>Best regards,<br>The CGV Movies Team</p>" +
                        "</div>";

        try {
            customerService.save(customer);
            mailService.sendForgetPasswordMail("Reset Your Password", content, customer.getEmail());
        } catch (RuntimeException e) {
            customer.setVerify("active");
            customer.setResetRequestedAt(null);
            customerService.save(customer);
            return ResponseEntity.status(502).body(Map.of(
                    "error", Map.of(
                            "code", "SEND_MAIL_FAILED",
                            "message", "Unable to send reset password email."
                    )
            ));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Please check your email to change password."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        if (request.id() == null || request.id().isBlank()) {
            return validationError("id", "Customer id is required.");
        }
        if (request.resetTimestamp() == null) {
            return validationError("resetTimestamp", "Reset timestamp is required.");
        }
        if (request.resetCode() == null || request.resetCode().isBlank()) {
            return validationError("resetCode", "Reset code is required.");
        }

        Customer customer = customerService.findCustomerById(request.id());
        if (customer == null
                || !"resetPassword".equals(customer.getVerify())
                || customer.getResetRequestedAt() == null
                || !resetPasswordTokenService.matchesStoredTimestamp(customer.getResetRequestedAt(), request.resetTimestamp())
                || !resetPasswordTokenService.isValid(request.id(), request.resetTimestamp(), request.resetCode())
                || LocalDateTime.now().isAfter(customer.getResetRequestedAt().plusMinutes(10))) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", Map.of(
                            "code", "INVALID_RESET_LINK",
                            "message", "This reset link is no longer valid."
                    )
            ));
        }

        Map<String, String> errors;
        try {
            errors = customerService.resetPassword(
                    request.id(),
                    request.newPassword(),
                    request.confirmPassword()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", Map.of(
                            "code", "RESET_PASSWORD_FAILED",
                            "message", "Unable to reset password."
                    )
            ));
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.unprocessableEntity().body(Map.of(
                    "error", Map.of(
                            "code", "VALIDATION_ERROR",
                            "message", "Invalid request data",
                            "details", errors
                    )
            ));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Password changed successfully."
        ));
    }

    private ResponseEntity<?> validationError(String field, String message) {
        return ResponseEntity.unprocessableEntity().body(Map.of(
                "error", Map.of(
                        "code", "VALIDATION_ERROR",
                        "message", "Invalid request data",
                        "details", Map.of(field, message)
                )
        ));
    }
}
