package vn.edu.fpt.cinemamanagement.dto;

import java.math.BigDecimal;

public record BankingPaymentResponseDTO(
        String paymentId,
        String bookingId,
        String staffId,
        BigDecimal amount,
        String paymentMethod,
        String paymentStatus,
        String qrContent,
        String qrImageUrl
) {
}
