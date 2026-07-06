package vn.edu.fpt.cinemamanagement.dto;

import vn.edu.fpt.cinemamanagement.entities.Movie;

import java.time.LocalDate;

public class MovieDTO {
    private String movieID;
    private String title;
    private String genre;
    private String summary;
    private int duration;
    private LocalDate releaseDate;
    private String ageRating;
    private String img;
    private String trailer;

    public MovieDTO() {
    }

    public MovieDTO(String movieID, String title, String genre, String summary, int duration, LocalDate releaseDate, String ageRating, String img, String trailer) {
        this.movieID = movieID;
        this.title = title;
        this.genre = genre;
        this.summary = summary;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.ageRating = ageRating;
        this.img = img;
        this.trailer = trailer;
    }

    public static MovieDTO fromEntity(Movie movie) {
        return new MovieDTO(
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

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }
}
