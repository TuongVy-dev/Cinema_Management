package vn.edu.fpt.cinemamanagement.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.cinemamanagement.entities.Voucher;
import vn.edu.fpt.cinemamanagement.repositories.VoucherRepository;

import java.util.List;

@Service
public class VoucherService {

    private VoucherRepository voucherRepo;

    public VoucherService(VoucherRepository voucherRepo) {
        this.voucherRepo = voucherRepo;
    }

    public List<Voucher> getAllVouchers() {
        return voucherRepo.findAll();
    }
}
