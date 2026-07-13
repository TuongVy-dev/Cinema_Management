package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.dto.request.ConcessionRequestDTO;
import vn.edu.fpt.cinemamanagement.dto.response.ConcessionResponse;
import vn.edu.fpt.cinemamanagement.dto.response.PageResponseDTO;
import vn.edu.fpt.cinemamanagement.services.IConcessionService;

import java.util.List;


@RestController
@RequestMapping("/api/concessions")
public class ConcessionController {

    private final IConcessionService service;

    public ConcessionController(IConcessionService service) {
        this.service = service;
    }

    // LIST + PAGINATION (y như voucher)
    @GetMapping("")
    public PageResponseDTO<ConcessionResponse> getAllConcessions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return service.getAllConcessions(page, pageSize);
    }

    @GetMapping("/{id}")
    public ConcessionResponse getById(@PathVariable String id) {
        return service.getConcessionById(id);
    }

    @PostMapping
    public ConcessionResponse create(@RequestBody ConcessionRequestDTO request) {
        return service.createConcession(request);
    }

    @PutMapping("/{id}")
    public ConcessionResponse update(
            @PathVariable String id,
            @RequestBody ConcessionRequestDTO request
    ) {
        return service.updateConcession(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteConcession(id);
    }

    @GetMapping("/images")
    public List<String> getImages() {
        return service.getImages();
    }
}
