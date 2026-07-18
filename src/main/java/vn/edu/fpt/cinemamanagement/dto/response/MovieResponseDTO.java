package vn.edu.fpt.cinemamanagement.dto.response;

import vn.edu.fpt.cinemamanagement.entities.Movie;

import java.time.LocalDate;

/**
 * Response payload để trả thông tin movie ra ngoài REST API.
 */
public record MovieResponseDTO(
        String movieID,
        String title,
        String genre,
        String summary,
        int duration,
        LocalDate releaseDate,
        String ageRating,
        String img,
        String trailer
) {
    public static MovieResponseDTO fromEntity(Movie movie) {
        return new MovieResponseDTO(
                movie.getMovieID(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getSummary(),
                movie.getDuration(),
                movie.getReleaseDate(),
                movie.getAgeRating(),
                movie.getImg(),
                movie.getTrailer()
        );
    }
}
