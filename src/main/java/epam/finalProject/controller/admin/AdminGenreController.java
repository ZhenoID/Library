package epam.finalProject.controller.admin;

import epam.finalProject.entity.Genre;
import epam.finalProject.service.GenreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/admin/genres")
@PreAuthorize("hasAnyAuthority('ADMIN','LIBRARIAN')")
public class AdminGenreController {
    private final GenreService genreService;
    private static final Logger logger = LoggerFactory.getLogger(AdminGenreController.class);

    public AdminGenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    /**
     * Shows the form of the creating new genre
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.info("GET /admin/genres/add — отображаем форму добавления жанра");
        model.addAttribute("genre", new Genre());
        return "admin/add-genre";
    }

    /**
     * Process post form of the creating new genre
     */
    @PostMapping("/add")
    public String addGenre(@ModelAttribute Genre genre) {
        logger.info("POST /admin/genres/add — получены данные для нового жанра: name='{}'", genre.getName());

        try {
            genreService.save(genre);
            logger.info("Жанр успешно сохранён: id={}, name='{}'", genre.getId(), genre.getName());
        } catch (Exception e) {
            logger.error("Ошибка при сохранении жанра: name='{}'. Подробности: {}", genre.getName(), e.getMessage(), e);
            return "admin/add-genre";
        }

        return "redirect:/admin/books/add";
    }



    /**
     * Shows the list of all genres
     */
    @GetMapping
    public String listGenres(Model model) {
        logger.info("GET /admin/genres — запрос списка жанров");
        List<Genre> genres = genreService.findAll();
        logger.debug("Найдено {} жанров для отображения", genres.size());
        model.addAttribute("genres", genres);
        return "admin/genre-list";
    }
}
