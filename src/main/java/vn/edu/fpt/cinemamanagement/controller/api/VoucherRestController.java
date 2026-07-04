package vn.edu.fpt.cinemamanagement.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.dto.request.VoucherApplyRequestDTO;
import vn.edu.fpt.cinemamanagement.dto.response.VoucherApplyResponseDTO;
import vn.edu.fpt.cinemamanagement.entities.Voucher;
import vn.edu.fpt.cinemamanagement.services.VoucherService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherRestController {

    private final VoucherService voucherService;

    public VoucherRestController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    // ========================== APPLY VOUCHER ==========================
    @PostMapping("/apply")
    public ResponseEntity<?> applyVoucher(@RequestBody VoucherApplyRequestDTO dto) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Voucher code is required."));
        }

        Optional<Voucher> optVoucher = voucherService.findByVoucherCode(dto.getCode().toUpperCase());
        if (optVoucher.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Voucher not found."));
        }

        Voucher voucher = optVoucher.get();

        // Check if voucher is active
        if (!voucher.isStatus()) {
            return ResponseEntity.badRequest().body(Map.of("error", "This voucher is no longer active."));
        }

        // Check if usage limit reached
        if (voucher.getUsedCount() >= voucher.getUsageLimit()) {
            return ResponseEntity.badRequest().body(Map.of("error", "This voucher has reached its usage limit."));
        }

        // Calculate discounted price (do NOT increase usedCount)
        double discountedPrice = voucherService.applyDiscount(voucher, dto.getTotalPrice());

        VoucherApplyResponseDTO response = new VoucherApplyResponseDTO(
                voucher.getVoucherId(),
                voucher.getDiscountType(),
                voucher.getDiscountValue(),
                dto.getTotalPrice(),
                discountedPrice
        );

        return ResponseEntity.ok(response);
    }
}
