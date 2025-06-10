package epam.finalProject.controller.admin;

import epam.finalProject.entity.Author;
import epam.finalProject.entity.Book;
import epam.finalProject.service.AuthorService;
import epam.finalProject.service.BookService;
import epam.finalProject.service.GenreService;
import jakarta.validation.Valid;                       // ← новый импорт
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;     // ← новый импорт
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN','LIBRARIAN')")
@RequestMapping("/admin/books")
public class AdminBookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private static final Logger logger = LoggerFactory.getLogger(AdminBookController.class);

    public AdminBookController(BookService bookService,
                               AuthorService authorService,
                               GenreService genreService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @GetMapping
    public String showBooks(Model model) {
        logger.info("GET /admin/books — запрос списка книг");
        model.addAttribute("books", bookService.findAll());
        return "admin/book-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.info("GET /admin/books/add — отображаем форму добавления книги");
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "admin/add-book";
    }

    @PostMapping("/add")
    public String addBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult br,
            Model model
    ) {
        logger.info("POST /admin/books/add — данные новой книги: title='{}'", book.getTitle());

        if (br.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "admin/add-book";
        }

        if (!authorService.existsById(book.getAuthorId())) {
            logger.warn("Автор с id={} не найден — создаём временного", book.getAuthorId());
            Author temp = new Author();
            temp.setId(book.getAuthorId());
            temp.setName("Unknown Author " + book.getAuthorId());
            authorService.save(temp);
        }
        Author author = authorService.findById(book.getAuthorId());

        if (bookService.saveBookWithAuthor(book, author)) {
            logger.info("Книга '{}' успешно сохранена (id={})", book.getTitle(), book.getId());
            return "redirect:/books";
        }

        logger.error("Не удалось добавить книгу '{}'", book.getTitle());
        model.addAttribute("error", "Failed to add book.");
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "admin/add-book";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit")
    public String editBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult br,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "admin/edit-book";
        }
        if (bookService.changeBook(book)) {
            return "redirect:/books";
        }
        model.addAttribute("error", "Failed to update book.");
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "admin/edit-book";
    }

}
