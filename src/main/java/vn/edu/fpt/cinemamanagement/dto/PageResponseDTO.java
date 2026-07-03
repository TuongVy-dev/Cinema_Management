package vn.edu.fpt.cinemamanagement.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponseDTO<T>(
        List<T> content,
        int pageNumber,
        int size,
        long totalElement,
        int totalPages,
        boolean last)
{
    public static <T> PageResponseDTO<T> of(Page<T> page){
        return new PageResponseDTO<T>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(), page.isLast()
        );
    }

}
