package vn.edu.fpt.cinemamanagement.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.edu.fpt.cinemamanagement.entities.Concession;
import vn.edu.fpt.cinemamanagement.repositories.ConcessionRepository;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConcessionService {

    private final ConcessionRepository repo;

    public ConcessionService(ConcessionRepository repo) {
        this.repo = repo;
    }

    /* ======================= PAGINATION ======================= */
    /** Controller truyền page (1-based) -> ở đây đổi sang 0-based cho Spring Data */
    public Page<Concession> findPage(int page1Based, int pageSize) {
        int pageIndex = Math.max(page1Based, 1) - 1;
        return repo.findAll(Pageable.ofSize(pageSize).withPage(pageIndex));
    }

    /* ======================= CRUD ======================= */
    public Concession findById(String id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Concession not found: " + id));
    }

    /** Tạo mới: gán prefix (PC/DR...), set img nếu truyền imageFile, validate tất cả, save */
    public Map<String, String> createWithPrefix(String type, Concession c, String imageFile) {
        Map<String, String> errors = new HashMap<>();

        // type
        if (!StringUtils.hasText(type)) {
            errors.put("type", "Type is required");
        }

        // set image nếu chọn từ list
        if (StringUtils.hasText(imageFile)) {
            c.setImg("/assets/img/concessions/" + imageFile);
        }

        // validate dữ liệu (yêu cầu có ảnh khi tạo)
        validateConcession(c, true, errors);

        if (!errors.isEmpty()) return errors;

        String newId = nextId(type.toUpperCase(Locale.ROOT));
        c.setConcessionId(newId);
        repo.save(c);
        return errors; // rỗng = OK
    }

    /** Update: set id, giữ ảnh cũ nếu không đổi, validate, save */
    public Map<String, String> update(String id, Concession incoming, String imageFile) {
        Map<String, String> errors = new HashMap<>();
        Concession old = findById(id);

        // merge các trường cho chắc (giữ nguyên những gì không đổi)
        old.setName(incoming.getName());
        old.setPrice(incoming.getPrice());
        old.setDescription(incoming.getDescription());
        if (StringUtils.hasText(imageFile)) {
            old.setImg("/assets/img/concessions/" + imageFile);
        }
        // validate (update không bắt buộc đổi ảnh -> requireImage=false)
        validateConcession(old, false, errors);

        if (!errors.isEmpty()) return errors;

        repo.save(old);
        return errors; // rỗng = OK
    }

    public void delete(String id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
        }
    }

    /* ======================= IMAGE PICKER ======================= */
    /** Liệt kê file ảnh dưới /static/assets/img/concessions (chạy dev). */
    public List<String> listImageFiles() {
        try {
            ClassPathResource root = new ClassPathResource("static/assets/img/concessions");
            File folder = root.getFile();
            if (folder.exists() && folder.isDirectory()) {
                return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                        .filter(File::isFile)
                        .map(File::getName)
                        .sorted()
                        .collect(Collectors.toList());
            }
        } catch (IOException ignored) {}
        return Collections.emptyList();
    }

    /* ======================= VALIDATION (thuần Java, không regex) ======================= */
    private void validateConcession(Concession c, boolean requireImage, Map<String, String> errors) {
        // Name: chỉ chữ/số/khoảng trắng, 2–50
        if (!StringUtils.hasText(c.getName())) {
            errors.put("name", "Name is required");
        } else if (!isAlnumSpace(c.getName(), 2, 50)) {
            errors.put("name", "Name: 2–50 chars, only letters, numbers, spaces");
        }

        // Price: >= 0
        if (c.getPrice() == null || c.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("price", "Price must be \u2265 0");
        }

        // Description: chữ/số/khoảng trắng/ , .
        if (!StringUtils.hasText(c.getDescription())) {
            errors.put("description", "Description is required");
        } else if (!isAlnumSpaceCommaDot(c.getDescription(), 5, 200)) {
            errors.put("description", "Description: 5–200 chars; only letters, numbers, spaces, comma, period");
        }

        // Image
        if (requireImage && !StringUtils.hasText(c.getImg())) {
            errors.put("img", "Please choose an image");
        }
    }

    private boolean isAlnumSpace(String s, int min, int max) {
        if (s == null) return false;
        s = s.trim();
        if (s.length() < min || s.length() > max) return false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!((ch >= 'A' && ch <= 'Z') ||
                    (ch >= 'a' && ch <= 'z') ||
                    (ch >= '0' && ch <= '9') ||
                    ch == ' ')) {
                return false;
            }
        }
        return true;
    }

    private boolean isAlnumSpaceCommaDot(String s, int min, int max) {
        if (s == null) return false;
        s = s.trim();
        if (s.length() < min || s.length() > max) return false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!((ch >= 'A' && ch <= 'Z') ||
                    (ch >= 'a' && ch <= 'z') ||
                    (ch >= '0' && ch <= '9') ||
                    ch == ' ' || ch == ',' || ch == '.')) {
                return false;
            }
        }
        return true;
    }

    /* ======================= ID HELPER ======================= */
    /**
     * Lấy ID cuối theo prefix (PC/DR...) rồi +1 (format PREFIX + 6 số).
     * Yêu cầu repo có method: Page<Concession> findByConcessionIdStartingWith(String prefix, Pageable pageable)
     */
    private String nextId(String prefix) {
        Pageable top1Desc = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "concessionId"));
        Page<Concession> page = repo.findByConcessionIdStartingWith(prefix, top1Desc);

        int next = 1;
        if (!page.isEmpty()) {
            String lastId = page.getContent().get(0).getConcessionId();
            if (lastId != null && lastId.length() > prefix.length()) {
                try {
                    next = Integer.parseInt(lastId.substring(prefix.length())) + 1;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("%s%06d", prefix, next);
    }
}
