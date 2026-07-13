package vn.edu.fpt.cinemamanagement.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.fpt.cinemamanagement.dto.PageResponseDTO;
import vn.edu.fpt.cinemamanagement.entities.Voucher;
import vn.edu.fpt.cinemamanagement.services.IVoucherService;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final IVoucherService voucherService;

    public VoucherController(IVoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping("/admin")
    public PageResponseDTO<Voucher> voucherList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Voucher> pageRes = voucherService.findAllVoucher(pageable);
        return PageResponseDTO.of(pageRes);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> detailVoucher(@PathVariable String id) {
        Voucher voucher = voucherService.findVoucherById(id);

        if (voucher == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(voucher);
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createVoucher(@RequestBody Voucher voucher) {

        Map<String, String> errors = voucherService.validateVoucher(voucher);
        if (!errors.isEmpty()) {
            return validationError(errors);
        }

        Voucher savedVoucher = voucherService.createVoucher(voucher);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVoucher);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateVoucher(@PathVariable String id, @RequestBody Voucher voucher) {
        Voucher existingVoucher = voucherService.findVoucherById(id);

        if (existingVoucher == null) {
            return ResponseEntity.notFound().build();
        }

        voucher.setVoucherId(id);

        Map<String, String> errors = voucherService.validateVoucher(voucher);
        if (!errors.isEmpty()) {
            return validationError(errors);
        }


        Voucher updatedVoucher = voucherService.updateVoucher(id, voucher);
        return ResponseEntity.ok(updatedVoucher);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable String id) {
        voucherService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> validationError(Map<String, String> errors) {
        return ResponseEntity.unprocessableEntity().body(Map.of(
                "error", Map.of(
                        "code", "VALIDATION_ERROR",
                        "message", "Invalid voucher data",
                        "details", errors
                )
        ));
    }
}

