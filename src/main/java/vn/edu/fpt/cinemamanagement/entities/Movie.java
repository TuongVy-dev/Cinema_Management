package vn.edu.fpt.cinemamanagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "Movie")
public class Movie {
    @Id
    @Column(name = "movie_id")
    private String movieID;

    private String title;
    private String genre;
    private String summary;
    private int duration;

    @Column(name = "release_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Column(name = "age_rating")
    private int ageRating;

    public Movie(){
    }
    public Movie(String movieID, String title, String genre, String summary, int duration, LocalDate  releaseDate, int ageRating) {
        this.movieID = movieID;
        this.title = title;
        this.genre = genre;
        this.summary = summary;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.ageRating = ageRating;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieId) {
        this.movieID = movieId;
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

    public LocalDate  getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate  releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(int ageRating) {
        this.ageRating = ageRating;
    }
}
