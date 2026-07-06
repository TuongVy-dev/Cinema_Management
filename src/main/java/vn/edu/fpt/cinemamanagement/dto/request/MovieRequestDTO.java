package vn.edu.fpt.cinemamanagement.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request payload để tạo / cập nhật movie qua REST API.
 * Chỉ chứa validate FORMAT (stateless) bằng Bean Validation.
 * Các rule nghiệp vụ (trùng title, giới hạn ngày chiếu, ảnh bắt buộc...)
 * vẫn được xử lý trong {@code MovieService}.
 */
public record MovieRequestDTO(

        @NotBlank(message = "The title is required.")
        @Size(min = 2, max = 100, message = "The title must be between 2 and 100 characters.")
        @Pattern(
                regexp = "^[A-Za-z0-9\\s.,:;!?'\"()\\-]{2,100}$",
                message = "The title can only contain letters, numbers, spaces, and basic punctuation marks."
        )
        String title,

        @NotBlank(message = "The movie genre is required.")
        String genre,

        @NotBlank(message = "The summary is required.")
        @Pattern(
                regexp = "^[A-Za-z0-9\\s.,:;!?'\"()\\-\\n]{10,1000}$",
                flags = Pattern.Flag.DOTALL,
                message = "The summary must be 10–1000 characters long and can only contain letters, numbers, spaces, basic punctuation, and line breaks."
        )
        String summary,

        @NotNull(message = "The duration is required.")
        @Min(value = 61, message = "Duration must be between 60 and 180 minutes.")
        @Max(value = 180, message = "Duration must be between 60 and 180 minutes.")
        Integer duration,

        @NotNull(message = "The release date is required.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate releaseDate,

        @NotBlank(message = "The age rating is required.")
        String ageRating,

        @NotBlank(message = "The trailer link is required.")
        @Pattern(
                regexp = "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[A-Za-z0-9_-]{11}(&\\S*)?$",
                message = "Please enter a valid YouTube link (e.g. https://youtu.be/XXXXXXXXXXX)."
        )
        String trailer
) {
}
