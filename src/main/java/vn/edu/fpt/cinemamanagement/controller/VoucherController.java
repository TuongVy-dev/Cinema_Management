package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.fpt.cinemamanagement.entities.Voucher;
import vn.edu.fpt.cinemamanagement.services.VoucherService;

@Controller
@RequestMapping("/vouchers")
public class VoucherController {

    private VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping("")
    public String vouchersList(Model model) {
        voucherService.findAllVoucher();
        model.addAttribute("vouchersList", voucherService.findAllVoucher());
        return "vouchers/voucher_list";
    }

    @GetMapping("/create")
    public String newVoucher(Model model) {
        model.addAttribute("voucher", new Voucher());
        return "vouchers/voucher_create";
    }

    @PostMapping("/create")
    public String createVoucher(Voucher voucher, Model model) {
        if (voucher.getDiscountType().equals("amount")) {
            voucher.setVoucherId(voucherService.generateAmountVoucherID());
        } else {
            voucher.setVoucherId(voucherService.generatePercentageVoucherID());
        }

        boolean isValid = voucherService.validateVoucher(model, voucher);
        if (!isValid) {
            model.addAttribute("voucher", voucher);
            return "vouchers/voucher_create";
        }

        voucher.setVoucherName(voucher.getVoucherName().toUpperCase());
        voucher.setCode(voucher.getCode().toUpperCase());
        voucher.setUsedCount(0);
        voucher.setStatus(true);
        voucherService.save(voucher);
        return "redirect:/vouchers";
    }

    @GetMapping("/update/{id}")
    public String editVoucher(Model model, @PathVariable("id") String id) {
        model.addAttribute("voucher", voucherService.findVoucherById(id));
        return "vouchers/voucher_update";
    }

    @PostMapping("/update")
    public String updateVoucher(Voucher voucher, Model model) {
        boolean isValid = voucherService.validateVoucher(model, voucher);
        if (!isValid) {
            model.addAttribute("voucher", voucher);
            return "vouchers/voucher_update";
        }

        voucher.setVoucherName(voucher.getVoucherName().toUpperCase());
        voucher.setCode(voucher.getCode().toUpperCase());
        voucherService.save(voucher);
        return "redirect:/vouchers";
    }



    @GetMapping("/detail/{id}")
    public String detailVoucher(Model model, @PathVariable("id") String id) {
        model.addAttribute("voucher", voucherService.findVoucherById(id));
        return "vouchers/voucher_detail";
    }
}

