package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.cinemamanagement.entities.Movie;
import vn.edu.fpt.cinemamanagement.services.MovieService;

import java.time.LocalDate;

@Controller
@RequestMapping(value = "movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @RequestMapping
    public String getAllMovies(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies/movie_list";
    }
    @RequestMapping(value = "/detail/{id}")
    public String getMovieDetails(@PathVariable("id") String id, Model model) {
        Movie movie = movieService.findById(id);
        model.addAttribute("movie", movie);
        return "movie_detail";
    }
    @RequestMapping(value = "create")
    public String createMovie(Model model) {
        model.addAttribute("movie", new Movie());
        return "movies/movie_create";
    }
    @RequestMapping(value = "/update/{id}")
    public String updateMovie(@PathVariable("id") String id, Model model) {
        model.addAttribute("movie", movieService.findById(id));
        return "movies/movie_update";
    }

    @PostMapping(value = "save")
    public String save(@ModelAttribute("movie") Movie movie) {
        // kiểm tra nếu null thì gán tạm
        if (movie.getReleaseDate() == null) {
            movie.setReleaseDate(LocalDate.now());
        }
        movieService.save(movie);
    return "redirect:/movies";
    }

    @RequestMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") String id, Model model) {
        Movie movie = movieService.findById(id);
        model.addAttribute("movie", movie);
        return  "movies/movie_delete";
    }

    @PostMapping(value = "/delete")
    public String delete(@ModelAttribute("movie") Movie movie) {
        movieService.delete(movie);
        return "redirect:/movies";
    }
}
