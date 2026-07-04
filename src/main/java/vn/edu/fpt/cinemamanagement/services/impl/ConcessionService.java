package vn.edu.fpt.cinemamanagement.services.impl;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.edu.fpt.cinemamanagement.dto.response.ConcessionResponse;
import vn.edu.fpt.cinemamanagement.dto.response.PageResponseDTO;
import vn.edu.fpt.cinemamanagement.entities.Concession;
import vn.edu.fpt.cinemamanagement.repositories.ConcessionRepository;
import vn.edu.fpt.cinemamanagement.services.IConcessionService;
import vn.edu.fpt.cinemamanagement.dto.request.ConcessionRequestDTO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConcessionService implements IConcessionService {

    private final ConcessionRepository repo;

    public ConcessionService(ConcessionRepository repo) {
        this.repo = repo;
    }

    /* ======================= PAGINATION ======================= */

    /**
     * Controller truyền page (1-based) -> ở đây đổi sang 0-based cho Spring Data
     */
    @Override
    public PageResponseDTO<ConcessionResponse> getAllConcessions(int page, int pageSize) {
        int pageIndex = Math.max(page, 1) - 1;
        int safePpageSize = Math.min(Math.max(pageSize, 1), 20);
        PageRequest pagable = PageRequest.of(pageIndex, safePpageSize, Sort.by(Sort.Direction.ASC, "concessionId"));
        Page<ConcessionResponse> responsePage =
                repo.findAll(pagable)
                        .map(this::toResponse);

        return PageResponseDTO.of(responsePage);
    }

    @Override
    public ConcessionResponse getConcessionById(String id) {
        return toResponse(findEntityById(id));
    }

    @Override
    public ConcessionResponse createConcession(ConcessionRequestDTO request) {
        Concession concession = new Concession();

        concession.setName(request.name());
        concession.setPrice(request.price());
        concession.setDescription(request.description());

        if (StringUtils.hasText(request.img())) {
            concession.setImg("/assets/img/concessions/" + request.img());
        }

        Map<String, String> errors = new HashMap<>();

        validateType(request.type(), errors);
        validateConcession(concession, true, errors);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }

        concession.setConcessionId(nextId(request.type().toUpperCase(Locale.ROOT)));

        Concession saved = repo.save(concession);

        return toResponse(saved);
    }

    @Override
    public ConcessionResponse updateConcession(String id, ConcessionRequestDTO request) {
        Concession old = findEntityById(id);

        old.setName(request.name());
        old.setPrice(request.price());
        old.setDescription(request.description());

        if (StringUtils.hasText(request.img())) {
            old.setImg("/assets/img/concessions/" + request.img());
        }

        Map<String, String> errors = new HashMap<>();
        validateConcession(old, false, errors);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }

        Concession saved = repo.save(old);

        return toResponse(saved);
    }

    @Override
    public void deleteConcession(String id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Concession not found: " + id);
        }

        repo.deleteById(id);
    }

    @Override
    public List<String> getImages() {
        return listImgs();
    }

    private Concession findEntityById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Concession not found: " + id));
    }

    private ConcessionResponse toResponse(Concession concession) {
        return new ConcessionResponse(
                concession.getConcessionId(),
                concession.getName(),
                concession.getPrice(),
                concession.getDescription(),
                concession.getImg()
        );
    }

    private void validateType(String type, Map<String, String> errors) {
        if (!StringUtils.hasText(type)) {
            errors.put("type", "Type is required");
            return;
        }

        String normalizedType = type.toUpperCase(Locale.ROOT);

        if (!normalizedType.equals("PC") && !normalizedType.equals("DR")) {
            errors.put("type", "Type must be PC or DR");
        }
    }

    private String nextId(String prefix) {
        Pageable top1Desc = PageRequest.of(
                0,
                1,
                Sort.by(Sort.Direction.DESC, "concessionId")
        );

        Page<Concession> page = repo.findByConcessionIdStartingWith(prefix, top1Desc);

        int next = 1;

        if (!page.isEmpty()) {
            String lastId = page.getContent().get(0).getConcessionId();

            if (lastId != null && lastId.length() > prefix.length()) {
                try {
                    next = Integer.parseInt(lastId.substring(prefix.length())) + 1;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return String.format("%s%06d", prefix, next);
    }

    private List<String> listImgs() {
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
        } catch (IOException ignored) {
        }

        return Collections.emptyList();
    }

    private void validateConcession(Concession concession, boolean requireImage, Map<String, String> errors) {
        if (!StringUtils.hasText(concession.getName())) {
            errors.put("name", "Name is required");
        } else if (!isAlnumSpace(concession.getName(), 2, 50)) {
            errors.put("name", "Name must be 2-50 English letters, numbers or spaces");
        }

        if (concession.getPrice() == null || concession.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("price", "Price must be >= 0");
        }

        if (!StringUtils.hasText(concession.getDescription())) {
            errors.put("description", "Description is required");
        } else if (!isAlnumSpaceCommaDot(concession.getDescription(), 5, 200)) {
            errors.put("description", "Description must be 5-200 English letters, numbers, spaces, commas or periods");
        }

        if (requireImage && !StringUtils.hasText(concession.getImg())) {
            errors.put("img", "Please choose an image");
        }
    }

    private boolean isAlnumSpace(String value, int min, int max) {
        if (value == null) return false;

        String trimmedValue = value.trim();

        if (trimmedValue.length() < min || trimmedValue.length() > max) return false;

        for (int i = 0; i < trimmedValue.length(); i++) {
            char ch = trimmedValue.charAt(i);

            if (!((ch >= 'A' && ch <= 'Z') ||
                    (ch >= 'a' && ch <= 'z') ||
                    (ch >= '0' && ch <= '9') ||
                    ch == ' ')) {
                return false;
            }
        }

        return true;
    }

    private boolean isAlnumSpaceCommaDot(String value, int min, int max) {
        if (value == null) return false;

        String trimmedValue = value.trim();

        if (trimmedValue.length() < min || trimmedValue.length() > max) return false;

        for (int i = 0; i < trimmedValue.length(); i++) {
            char ch = trimmedValue.charAt(i);

            if (!((ch >= 'A' && ch <= 'Z') ||
                    (ch >= 'a' && ch <= 'z') ||
                    (ch >= '0' && ch <= '9') ||
                    ch == ' ' ||
                    ch == ',' ||
                    ch == '.')) {
                return false;
            }
        }

        return true;
    }
}
