package vn.edu.fpt.cinemamanagement.services;

import vn.edu.fpt.cinemamanagement.dto.request.ConcessionRequestDTO;
import vn.edu.fpt.cinemamanagement.dto.response.ConcessionResponse;
import vn.edu.fpt.cinemamanagement.dto.response.PageResponseDTO;

import java.util.List;

public interface IConcessionService {
    PageResponseDTO<ConcessionResponse> getAllConcessions(int page, int size);

    ConcessionResponse getConcessionById(String id);

    ConcessionResponse createConcession(ConcessionRequestDTO inputConcession);

    ConcessionResponse updateConcession(String id, ConcessionRequestDTO inputConcession);

    void deleteConcession(String id);

    List<String> getImages();

}
