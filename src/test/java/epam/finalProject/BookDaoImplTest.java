package epam.finalProject;

import epam.finalProject.DAO.AuthorDaoImpl;
import epam.finalProject.DAO.BookDaoImpl;
import epam.finalProject.entity.Author;
import epam.finalProject.entity.Book;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookDaoImplTest {

    private static DataSource ds;
    private BookDaoImpl bookDao;
    private AuthorDaoImpl authorDao;

    @BeforeAll
    static void initDatabase() throws Exception {
        // 1) Настраиваем H2 in-memory
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        h2.setUser("sa");
        h2.setPassword("");
        ds = h2;

        // 2) Создаём нужные таблицы
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE authors (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  name VARCHAR(255) UNIQUE NOT NULL
                );
                """);

            stmt.execute("""
                CREATE TABLE books (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  title VARCHAR(255) NOT NULL,
                  author_id BIGINT NOT NULL,
                  year INT,
                  description VARCHAR(1000),
                  FOREIGN KEY(author_id) REFERENCES authors(id)
                );
                """);
        }
    }

    @BeforeEach
    void setUp() {
        bookDao = new BookDaoImpl(ds);
        authorDao = new AuthorDaoImpl(ds);
    }

    @AfterEach
    void cleanUp() throws Exception {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE books");
            stmt.execute("TRUNCATE TABLE authors");
        }
    }

}
