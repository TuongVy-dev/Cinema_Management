package vn.edu.fpt.cinemamanagement.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.entities.ConcessionEntity;
import vn.edu.fpt.cinemamanagement.services.ConcessionService;

import java.util.List;

@Controller
@RequestMapping("/dashboard/concession")
public class ConcessionController {

    private final ConcessionService service;

    public ConcessionController(ConcessionService service) {
        this.service = service;
    }

    // LIST
    @GetMapping
    public String list(Model model) {
        List<ConcessionEntity> items = service.findAll();
        model.addAttribute("pageTitle", "Concessions");
        model.addAttribute("concessions", items);
        return "concession/concession_list";
    }

    // CREATE - form
    @GetMapping("/create")
    public String createForm(Model model) {
        ConcessionEntity c = new ConcessionEntity();
        c.setStatus(true);
        model.addAttribute("pageTitle", "Create Concession");
        model.addAttribute("concession", c);
        return "concession/concession_create";
    }

    // CREATE - submit (type = "PC" | "DR")
    @PostMapping("/create")
    public String create(@ModelAttribute("concession") ConcessionEntity c,
                         @RequestParam("type") String type) {
        String prefix = "PC".equalsIgnoreCase(type) ? "PC"
                : "DR".equalsIgnoreCase(type) ? "DR" : "";
        if (prefix.isEmpty()) {
            throw new IllegalArgumentException("Invalid type. Use PC (Popcorn) or DR (Drink).");
        }
        service.create(c, prefix);
        return "redirect:/dashboard/concession";
    }

    // DETAIL
    @GetMapping("/{concessionId}/detail")
    public String detail(@PathVariable("concessionId") String id, Model model) {
        ConcessionEntity c = service.findById(id);
        model.addAttribute("pageTitle", "Concession Detail");
        model.addAttribute("concession", c);
        return "concession/concession_detail";
    }

    // EDIT - form
    @GetMapping("/{concessionId}/edit")
    public String editForm(@PathVariable("concessionId") String id, Model model) {
        ConcessionEntity c = service.findById(id);
        model.addAttribute("pageTitle", "Edit Concession");
        model.addAttribute("concession", c);
        return "concession/concession_update";
    }

    // EDIT - submit
    @PostMapping("/{concessionId}/edit")
    public String update(@PathVariable("concessionId") String id,
                         @ModelAttribute("concession") ConcessionEntity c) {
        service.update(id, c);
        return "redirect:/dashboard/concession";
    }

    // DELETE
    @PostMapping("/{concessionId}/delete")
    public String delete(@PathVariable("concessionId") String id) {
        service.delete(id);
        return "redirect:/dashboard/concession";
    }
}

