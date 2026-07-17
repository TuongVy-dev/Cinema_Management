package vn.edu.fpt.cinemamanagement.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.edu.fpt.cinemamanagement.entities.Voucher;
import vn.edu.fpt.cinemamanagement.repositories.VoucherRepository;

@Service
public class VoucherService implements IVoucherService {

    private VoucherRepository voucherRepo;

    public VoucherService(VoucherRepository voucherRepo) {
        this.voucherRepo = voucherRepo;
    }

    public Page<Voucher> findAllVoucher(Pageable pageable) {
        return voucherRepo.findAll(pageable);
    }

    public List<Voucher> findAllVouchers() {
        return voucherRepo.findAll();
    }

    public Voucher findVoucherById(String ID) {
        return voucherRepo.findById(ID).orElse(null);
    }

//    public Voucher save(Voucher voucher) {
//        return voucherRepo.save(voucher);
//    }

    public Voucher createVoucher(Voucher voucher) {
    if ("amount".equalsIgnoreCase(voucher.getDiscountType())) {
        voucher.setVoucherId(generateAmountVoucherID());
    } else {
        voucher.setVoucherId(generatePercentageVoucherID());
    }

    voucher.setVoucherName(voucher.getVoucherName().toUpperCase());
    voucher.setCode(voucher.getCode().toUpperCase());
    voucher.setUsedCount(0);
    voucher.setStatus(true);

    return voucherRepo.save(voucher);
    }

    public Voucher updateVoucher(String id, Voucher input) {
    Voucher existingVoucher = voucherRepo.findById(id).orElse(null);

    if (existingVoucher == null) {
        return null;
    }

    existingVoucher.setVoucherName(input.getVoucherName().toUpperCase());
    existingVoucher.setCode(input.getCode().toUpperCase());
    existingVoucher.setUsageLimit(input.getUsageLimit());
    existingVoucher.setDiscountValue(input.getDiscountValue());
    existingVoucher.setStatus(input.isStatus());

    return voucherRepo.save(existingVoucher);
    }

    private int extractNextNumber(String maxID) {
        if (maxID == null || maxID.length() < 4) {
            return 1; // bắt đầu từ 00001 nếu chưa có mã
        }
        String numberPart = maxID.substring(3); // Bỏ VC1 / VC2
        return Integer.parseInt(numberPart) + 1;
    }

    private String generateAmountVoucherID() {
        String maxID = voucherRepo.getMaxAmountVoucherID();
        int nextNumber = extractNextNumber(maxID);

        return String.format("VC1%05d", nextNumber);
    }

    private String generatePercentageVoucherID() {
        String maxID = voucherRepo.getMaxPercentageVoucherID();
        int nextNumber = extractNextNumber(maxID);

        return String.format("VC2%05d", nextNumber);
    }

//    public boolean validateVoucher(Model model, Voucher voucher) {
//        boolean isValid = true;
//        List<Voucher> voucherList = voucherRepo.findAll();
//
//        //Validate Name
//        if (voucher.getVoucherName().isEmpty()) {
//            model.addAttribute("errorName", "Voucher name must be not empty!");
//            isValid = false;
//        } else if (voucher.getVoucherName().length() < 3) {
//            model.addAttribute("errorName", "Voucher name too short, must be greater than 3 characters");
//            isValid = false;
//        } else if (voucher.getVoucherName().length() > 100) {
//            model.addAttribute("errorName", "Voucher name too long");
//            isValid = false;
//        } else if (!voucher.getVoucherName().matches("^[a-zA-Z0-9\\s]+$")) {
//            model.addAttribute("errorName", "Voucher name must be not include special characters");
//            isValid = false;
//        }
//
//        //Validate code
//        if (voucher.getCode().isEmpty()) {
//            model.addAttribute("errorCode", "Voucher code must be not empty!");
//            isValid = false;
//        } else if (voucher.getCode().length() < 3) {
//            model.addAttribute("errorCode", "Voucher code too short, must be greater than 3 characters");
//            isValid = false;
//        } else if (!voucher.getCode().matches("^[a-zA-Z0-9\\s]+$")) {
//            model.addAttribute("errorCode", "Voucher code must be not include special characters");
//            isValid = false;
//        } else if (voucher.getCode().length() > 10) {
//            model.addAttribute("errorCode", "Voucher name too long, must be less than or equal to 10 characters");
//            isValid = false;
//        } else if (voucher.getCode().split(" ").length > 1) {
//            model.addAttribute("errorCode", "Voucher must be not have space");
//            isValid = false;
//        }
//        for (Voucher v : voucherList) {
//            if (voucher.getCode().equalsIgnoreCase(v.getCode()) && !voucher.getVoucherId().equalsIgnoreCase(v.getVoucherId())) {
//                model.addAttribute("errorCode", "Voucher code is available");
//                isValid = false;
//            }
//        }
//
//        //Validate Usage Limit
//        try {
//            if (voucher.getUsageLimit() <= 0) {
//                model.addAttribute("errorUsage", "Usage Limit number must be greater than 0");
//                isValid = false;
//            } else if (voucher.getUsageLimit() > 100000) {
//                model.addAttribute("errorUsage", "Usage Limit number is large, must be less than 100.000");
//                isValid = false;
//            }
//        } catch (Exception e) {
//            model.addAttribute("errorUsage",
//                    "Usage Limit is large");
//            isValid = false;
//        }
//
//        //Validate DiscountValue
//        try {
//            if (voucher.getDiscountType().equalsIgnoreCase("percentage")) {
//                if (voucher.getDiscountValue() <= 0 || voucher.getDiscountValue() >= 100) {
//                    model.addAttribute("errorValue",
//                            "Discount value must be greater than 0 and less than 100");
//                    isValid = false;
//                }
//            } else {
//                if (voucher.getDiscountValue() <= 1000) {
//                    model.addAttribute("errorValue",
//                            "Discount value must be greater than 1.000VNĐ");
//                    isValid = false;
//                } else if (voucher.getDiscountValue() > 10000000) {
//                    model.addAttribute("errorValue",
//                            "Discount value is large");
//                    isValid = false;
//                }
//            }
//        } catch (Exception e) {
//            model.addAttribute("errorValue",
//                    "Discount value is large");
//            isValid = false;
//        }
//        return isValid;
//    }


    public Map<String, String> validateVoucher(Voucher voucher) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (voucher.getVoucherName() == null || voucher.getVoucherName().isBlank()) {
            errors.put("voucherName", "Voucher name must be not empty!");
        } else if (voucher.getVoucherName().length() < 3) {
            errors.put("voucherName", "Voucher name too short, must be greater than 3 characters");
        } else if (voucher.getVoucherName().length() > 100) {
            errors.put("voucherName", "Voucher name too long");
        } else if (!voucher.getVoucherName().matches("^[a-zA-Z0-9\\s]+$")) {
            errors.put("voucherName", "Voucher name must be not include special characters");
        }

        if (voucher.getCode() == null || voucher.getCode().isBlank()) {
            errors.put("code", "Voucher code must be not empty!");
        } else if (voucher.getCode().length() < 3) {
            errors.put("code", "Voucher code too short, must be greater than 3 characters");
        } else if (!voucher.getCode().matches("^[a-zA-Z0-9\\s]+$")) {
            errors.put("code", "Voucher code must be not include special characters");
        } else if (voucher.getCode().length() > 10) {
            errors.put("code", "Voucher code must be less than or equal to 10 characters");
        } else if (voucher.getCode().contains(" ")) {
            errors.put("code", "Voucher must be not have space");
        }

        if (voucher.getUsageLimit() == null) {
            errors.put("usageLimit", "Usage Limit is required");
        } else if (voucher.getUsageLimit() <= 0) {
            errors.put("usageLimit", "Usage Limit number must be greater than 0");
        } else if (voucher.getUsageLimit() > 100000) {
            errors.put("usageLimit", "Usage Limit number is large, must be less than 100.000");
        }

        if (voucher.getDiscountType() == null || voucher.getDiscountType().isBlank()) {
            errors.put("discountType", "Discount type is required");
        } else if (voucher.getDiscountValue() == null) {
            errors.put("discountValue", "Discount value is required");
        } else if (voucher.getDiscountType().equalsIgnoreCase("percentage")) {
            if (voucher.getDiscountValue() <= 0 || voucher.getDiscountValue() >= 100) {
                errors.put("discountValue", "Discount value must be greater than 0 and less than 100");
            }
        } else {
            if (voucher.getDiscountValue() <= 1000) {
                errors.put("discountValue", "Discount value must be greater than 1.000VNĐ");
            } else if (voucher.getDiscountValue() > 10000000) {
                errors.put("discountValue", "Discount value is large");
            }
        }

        return errors;
    }

    public void delete(String id) {
        voucherRepo.deleteById(id);
    }


    public double applyDiscount(Voucher voucher, double totalPrice) {
        double discountValue = voucher.getDiscountValue();
        String type = voucher.getDiscountType();

        double newPrice;

        if ("Percentage".equalsIgnoreCase(type)) {
            newPrice = totalPrice * (1 - (discountValue / 100));
        } else {
            newPrice = totalPrice - discountValue;
        }

        if (newPrice < 0) newPrice = 0;
        return newPrice;
    }

    public Optional<Voucher> findByVoucherCode(String code) {
        return Optional.ofNullable(voucherRepo.findByVoucherCode(code));
    }
}
