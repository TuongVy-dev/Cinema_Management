package vn.edu.fpt.cinemamanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.cinemamanagement.entities.Voucher;
import vn.edu.fpt.cinemamanagement.service.VoucherService;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {

    Voucher findByVoucherId(String voucherId);
}
