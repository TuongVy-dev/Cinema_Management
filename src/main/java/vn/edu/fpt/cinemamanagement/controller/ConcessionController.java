package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.entities.Concession;
import vn.edu.fpt.cinemamanagement.services.ConcessionService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/concessions")
public class ConcessionController {

    private final ConcessionService service;
    private final ResourcePatternResolver resolver;

    public ConcessionController(ConcessionService service, ResourcePatternResolver resolver) {
        this.service = service;
        this.resolver = resolver;
    }

    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("pageTitle", "Concessions");
        model.addAttribute("concessions", service.findAll());
        return "concession/concession_list";
    }

    @GetMapping("/{concessionId}/detail")
    public String detail(@PathVariable("concessionId") String id, Model model) {
        Concession c = service.findById(id);
        model.addAttribute("pageTitle", "Concession Detail");
        model.addAttribute("concession", c);
        return "concession/concession_detail";
    }

    @GetMapping("/create")
    public String createForm(Model model) throws IOException {
        model.addAttribute("pageTitle", "Create Concession");
        model.addAttribute("concession", new Concession());
        model.addAttribute("imageFiles", listImageFiles());
        return "concession/concession_create";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam("type") String type,
            @RequestParam("imageFile") String imageFile,
            @ModelAttribute("concession") Concession concession,
            Model model) throws IOException {

        try {
            service.create(concession, type, imageFile);
            return "redirect:/concessions";
        } catch (BindException ex) {
            model.addAttribute("errors", toErrorMap(ex));
            model.addAttribute("imageFiles", listImageFiles());
            return "concession/concession_create";
        }
    }

    @GetMapping("/{concessionId}/edit")
    public String editForm(@PathVariable String concessionId, Model model) throws IOException {
        Concession c = service.findById(concessionId);
        model.addAttribute("pageTitle", "Edit Concession");
        model.addAttribute("concession", c);
        model.addAttribute("imageFiles", listImageFiles());
        return "concession/concession_update";
    }

    @PostMapping("/{concessionId}/edit")
    public String edit(
            @PathVariable String concessionId,
            @RequestParam(name = "imageFile", required = false) String imageFile,
            @ModelAttribute("concession") Concession concession,
            Model model) throws IOException {

        try {
            service.update(concessionId, concession, imageFile);
            return "redirect:/concessions/" + concessionId + "/detail";
        } catch (BindException ex) {
            model.addAttribute("errors", toErrorMap(ex));
            model.addAttribute("imageFiles", listImageFiles());
            return "concession/concession_update";
        }
    }

    @PostMapping("/{concessionId}/delete")
    public String delete(@PathVariable String concessionId) {
        service.delete(concessionId);
        return "redirect:/concessions";
    }

    // ===== Helpers =====
    private List<String> listImageFiles() throws IOException {
        List<String> files = new ArrayList<>();
        Resource[] resources = resolver.getResources("classpath:/static/assets/img/concessions/*.png");
        for (Resource r : resources) {
            files.add(r.getFilename());
        }
        return files;
    }

    private Map<String, String> toErrorMap(BindException ex) {
        Map<String, String> map = new LinkedHashMap<>();
        // Field errors
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            // nếu trùng field, giữ lỗi đầu
            map.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        // Global errors
        String global = ex.getBindingResult().getGlobalErrors().stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        if (!global.isBlank()) map.put("_global", global);
        return map;
    }
}
