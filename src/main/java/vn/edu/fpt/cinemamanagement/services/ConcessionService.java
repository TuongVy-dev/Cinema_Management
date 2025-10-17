package vn.edu.fpt.cinemamanagement.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import vn.edu.fpt.cinemamanagement.entities.Concession;
import vn.edu.fpt.cinemamanagement.repositories.ConcessionRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ConcessionService {

    private final ConcessionRepository repo;

    public ConcessionService(ConcessionRepository repo) {
        this.repo = repo;
    }

    public java.util.List<Concession> findAll() { return repo.findAll(); }

    public Concession findById(String id) { return repo.findById(id).orElse(null); }

    @Transactional
    public Concession create(Concession input, String typePrefix, String imageFile) throws BindException {
        // set ID + img trước khi validate
        String id = generateId(typePrefix);
        input.setConcessionId(id);
        if (StringUtils.hasText(imageFile)) {
            input.setImg("/assets/img/concessions/" + imageFile);
        }

        validateOrThrow(input, false);
        return repo.save(input);
    }

    @Transactional
    public Concession update(String id, Concession incoming, String imageFile) throws BindException {
        Optional<Concession> opt = repo.findById(id);
        if (opt.isEmpty()) {
            BeanPropertyBindingResult br = new BeanPropertyBindingResult(incoming, "concession");
            br.reject("", "Concession not found");
            throw new BindException(br);
        }

        Concession c = opt.get();
        c.setName(incoming.getName());
        c.setPrice(incoming.getPrice());
        c.setDescription(incoming.getDescription());
        if (StringUtils.hasText(imageFile)) {
            c.setImg("/assets/img/concessions/" + imageFile);
        }

        validateOrThrow(c, true);
        return repo.save(c);
    }

    @Transactional
    public void delete(String id) { repo.deleteById(id); }

    // ================= Helpers =================

    private String generateId(String typePrefix) {
        String prefix = "PC";
        if ("DR".equalsIgnoreCase(typePrefix)) prefix = "DR";

        // Repo của bạn trả về Optional<Concession>
        Optional<Concession> lastOpt =
                repo.findTopByConcessionIdStartingWithOrderByConcessionIdDesc(prefix);
        String last = lastOpt.map(Concession::getConcessionId).orElse(null);

        int next = 1;
        if (last != null && last.length() == 6) {
            try { next = Integer.parseInt(last.substring(2)) + 1; } catch (NumberFormatException ignored) {}
        }
        return String.format("%s%04d", prefix, next);
    }

    /** Gom validation về Service và ném BindException nếu có lỗi. */
    private void validateOrThrow(Concession c, boolean isUpdate) throws BindException {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(c, "concession");

        if (isUpdate) {
            if (!StringUtils.hasText(c.getConcessionId()) || !c.getConcessionId().matches("^[A-Z]{2}\\d{4}$")) {
                br.rejectValue("concessionId", "", "Invalid ID format (expected: PC0001 / DR0001)");
            }
        }

        if (!StringUtils.hasText(c.getName())) {
            br.rejectValue("name", "", "Name is required");
        } else if (c.getName().length() > 100) {
            br.rejectValue("name", "", "Name must be at most 100 characters");
        }

        BigDecimal p = c.getPrice();
        if (p == null) {
            br.rejectValue("price", "", "Price is required");
        } else {
            if (p.scale() > 2) br.rejectValue("price", "", "Price must have at most 2 decimals");
            if (p.signum() <= 0) br.rejectValue("price", "", "Price must be greater than 0");
            if (p.precision() - p.scale() > 8) br.rejectValue("price", "", "Price too large (max 8 digits before decimal)");
        }

        if (!StringUtils.hasText(c.getDescription())) {
            br.rejectValue("description", "", "Description is required");
        } else if (c.getDescription().length() > 255) {
            br.rejectValue("description", "", "Description must be at most 255 characters");
        }

        String img = c.getImg();
        if (!StringUtils.hasText(img)) {
            br.rejectValue("img", "", "Image path is required");
        } else {
            if (img.length() > 255) br.rejectValue("img", "", "Image path must be at most 255 characters");
            if (!img.startsWith("/assets/img/concessions/")) {
                br.rejectValue("img", "", "Image must be under /assets/img/concessions/");
            }
        }

        if (br.hasErrors()) throw new BindException(br);
    }
}
