package vn.edu.fpt.cinemamanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.cinemamanagement.entities.Voucher;
import vn.edu.fpt.cinemamanagement.services.VoucherService;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {

    @Query(value = "SELECT TOP 1 voucher_id FROM Voucher WHERE voucher_id LIKE 'VC1%' ORDER BY voucher_id DESC", nativeQuery = true)
    public String getMaxAmountVoucherCode();

    @Query(value = "SELECT TOP 1 voucher_id FROM Voucher WHERE voucher_id LIKE 'VC2%' ORDER BY voucher_id DESC", nativeQuery = true)
    public String getMaxPercentageVoucher();
}
