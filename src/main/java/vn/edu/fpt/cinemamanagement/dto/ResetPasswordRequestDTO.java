package vn.edu.fpt.cinemamanagement.dto;

public record ResetPasswordRequestDTO(
        String id,
        Long resetTimestamp,
        String resetCode,
        String newPassword,
        String confirmPassword
) {
}
