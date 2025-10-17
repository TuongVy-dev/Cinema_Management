package vn.edu.fpt.cinemamanagement.services;

import org.springframework.stereotype.Service;
import vn.edu.fpt.cinemamanagement.entities.Voucher;
import vn.edu.fpt.cinemamanagement.repositories.VoucherRepository;

import java.util.List;
import java.util.UUID;

@Service
public class VoucherService {

    private VoucherRepository voucherRepo;

    public VoucherService(VoucherRepository voucherRepo) {
        this.voucherRepo = voucherRepo;
    }

    public  List<Voucher> findAllVoucher(){
        return voucherRepo.findAll();
    }
    public Voucher findVoucherById(String ID){
        return voucherRepo.findById(ID).orElse(null);
    }
    public Voucher saveVoucher(Voucher voucher){
        return voucherRepo.save(voucher);
    }

    private int extractNextNumber(String maxCode) {
        if (maxCode == null || maxCode.length() < 4) {
            return 1; // bắt đầu từ 00001 nếu chưa có mã
        }
        String numberPart = maxCode.substring(3); // Bỏ VC1 / VC2
        return Integer.parseInt(numberPart) + 1;
    }

    public String generateAmountVoucherID(){
        String maxCode = voucherRepo.getMaxAmountVoucherCode();
        int nextNumber = extractNextNumber(maxCode);

        return String.format("VC1%05d", nextNumber);
    }

    public String generatePercentageVoucherID(){
        String maxCode = voucherRepo.getMaxPercentageVoucher();
        int nextNumber = extractNextNumber(maxCode);

        return String.format("VC2%05d", nextNumber);
    }




}
