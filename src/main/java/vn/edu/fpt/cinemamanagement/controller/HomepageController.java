package vn.edu.fpt.cinemamanagement.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.fpt.cinemamanagement.entities.Movie;
import vn.edu.fpt.cinemamanagement.services.MovieService;

import java.util.List;
@Controller


public class HomepageController {
    @Autowired
    private MovieService movieService;

    //Huynh Anh add
    @GetMapping({"/", "/homepage"})
    public String homepage(Model model) {
        List<Movie> nowShowing = movieService.getNowShowingMovies();
        model.addAttribute("nowShowing", nowShowing);
        return "homepage/homepage";
    }
}
