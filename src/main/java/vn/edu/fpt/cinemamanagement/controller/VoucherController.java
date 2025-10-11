package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.fpt.cinemamanagement.service.VoucherService;

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

    @GetMapping("/new")
    public String newVoucher(Model model) {
        return  "vouchers/voucher_create";
    }

//    @GetMapping("/detail/{id}")
//    public String detailVoucher(Model model, @PathVariable("id") String id) {
//        model.addAttribute("voucher", voucherService.findVoucherByID(id));
//        return "vouchers/voucher_detail";
//    }
}

