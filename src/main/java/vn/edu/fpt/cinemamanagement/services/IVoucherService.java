package vn.edu.fpt.cinemamanagement.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.cinemamanagement.entities.Voucher;

import java.util.Map;
import java.util.Optional;

public interface IVoucherService {
    Page<Voucher> findAllVoucher(Pageable pageable);
    Voucher findVoucherById(String ID);
    Voucher createVoucher(Voucher voucher);
    Voucher updateVoucher(String id, Voucher input);
    Map<String, String> validateVoucher(Voucher voucher);
    void delete(String id);
    double applyDiscount(Voucher voucher, double totalPrice);
    Optional<Voucher> findByVoucherCode(String code);

}
