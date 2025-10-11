package vn.edu.fpt.cinemamanagement.services;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.cinemamanagement.entities.ConcessionEntity;
import vn.edu.fpt.cinemamanagement.repositories.ConcessionReposive; // đúng tên interface của bạn

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;


@Service
@Transactional
public class ConcessionService {

    private static final Pattern PC = Pattern.compile("^PC\\d{4}$");
    private static final Pattern DR = Pattern.compile("^DR\\d{4}$");

    private final ConcessionReposive repo;

    public ConcessionService(ConcessionReposive repo) { this.repo = repo; }

    public List<ConcessionEntity> findAll() { return repo.findAll(); }

    public ConcessionEntity findById(String id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Not found: " + id));
    }

    /** Create: truyền "PC" (Popcorn) hoặc "DR" (Drink) để sinh ID */
    public ConcessionEntity create(ConcessionEntity c, String prefix) {
        if (c.getStatus() == null) c.setStatus(true);
        c.setConcessionId(ensureIdWithPrefix(c.getConcessionId(), prefix));
        return repo.save(c);
    }

    public ConcessionEntity update(String id, ConcessionEntity input) {
        ConcessionEntity cur = findById(id); // ID immutable
        cur.setName(input.getName());
        cur.setPrice(input.getPrice());
        cur.setDescription(input.getDescription());
        cur.setStatus(input.getStatus());
        return repo.save(cur);
    }

    public void delete(String id) { repo.deleteById(id); }

    // ===== Helpers =====
    public String ensureIdWithPrefix(String current, String prefix) {
        if (current != null && !current.isBlank()) {
            validateIdPrefix(current, prefix);
            return current;
        }
        return nextRunningId(prefix);
    }

    public void validateIdPrefix(String id, String expectedPrefix) {
        if ("PC".equalsIgnoreCase(expectedPrefix) && !PC.matcher(id).matches())
            throw new IllegalArgumentException("ID must match PCxxxx (Popcorn).");
        if ("DR".equalsIgnoreCase(expectedPrefix) && !DR.matcher(id).matches())
            throw new IllegalArgumentException("ID must match DRxxxx (Drink).");
    }

    private String nextRunningId(String prefix) {
        String p = prefix == null ? "" : prefix.toUpperCase();
        if (!p.equals("PC") && !p.equals("DR"))
            throw new IllegalArgumentException("Invalid type/prefix: " + prefix);

        Optional<ConcessionEntity> top =
                repo.findTopByConcessionIdStartingWithOrderByConcessionIdDesc(p);

        int next = top.map(e -> e.getConcessionId())
                .filter(id -> id.length() >= 6)
                .map(id -> id.substring(2))
                .map(s -> { try { return Integer.parseInt(s); } catch (Exception ex) { return 0; } })
                .orElse(0) + 1;

        if (next > 9999) throw new IllegalStateException("ID overflow for prefix " + p);
        return p + String.format("%04d", next);
    }

    // (tuỳ: giữ tương thích)
    public List<ConcessionEntity> list() { return findAll(); }
    public ConcessionEntity get(String id) { return repo.findById(id).orElse(null); }
}
