package vn.edu.fpt.cinemamanagement.services;

import vn.edu.fpt.cinemamanagement.repositories.TemplateSeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TemplateSeatService {
    private TemplateSeatRepository templateSeatRepository;

    public TemplateSeatService(TemplateSeatRepository templateSeatRepository) {
        this.templateSeatRepository = templateSeatRepository;
    }

    @Transactional
    public int countTotalSeatsByTemplateID(String templateID) {
        long total = templateSeatRepository.countByTemplate_Id(templateID);
        return (int) total;
    }
}

