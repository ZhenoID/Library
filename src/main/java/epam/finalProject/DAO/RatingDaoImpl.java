package epam.finalProject.DAO;

import epam.finalProject.db.ConnectionPool;
import epam.finalProject.entity.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of {@link RatingDao}.
 * Provides methods to save a rating and retrieve ratings by book or by user.
 * Uses {@link ConnectionPool} to obtain database connections.
 */
public class RatingDaoImpl implements RatingDao {

    private static final Logger logger = LoggerFactory.getLogger(RatingDaoImpl.class);

    /**
     * Obtains a database connection from the connection pool.
     *
     * @return a new {@link Connection}
     * @throws SQLException if a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        logger.debug("Acquiring connection from ConnectionPool");
        return ConnectionPool.getInstance().getConnection();
    }

    /**
     * Saves a new {@link Rating} into the database.
     *
     * @param rating the Rating entity to save (must contain userId, bookId, and score)
     * @return {@code true} if the insertion succeeded, {@code false} otherwise
     */
    @Override
    public boolean save(Rating rating) {
        String sql = "INSERT INTO ratings (user_id, book_id, score) VALUES (?, ?, ?)";
        logger.info("save(Rating) called for userId={}, bookId={}, score={}",
                rating.getUserId(), rating.getBookId(), rating.getScore());

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, rating.getUserId());
            ps.setLong(2, rating.getBookId());
            ps.setInt(3, rating.getScore());
            logger.debug("Executing INSERT: {} with values userId={}, bookId={}, score={}",
                    sql, rating.getUserId(), rating.getBookId(), rating.getScore());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No rows inserted for Rating: {}", rating);
                return false;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long generatedId = keys.getLong(1);
                    rating.setId(generatedId);
                    logger.info("Rating saved with generated ID={}", generatedId);
                }
            }
            return true;
        } catch (SQLException e) {
            logger.error("Database error in save(Rating) for userId={}, bookId={}, score={}",
                    rating.getUserId(), rating.getBookId(), rating.getScore(), e);
            return false;
        }
    }

    /**
     * Retrieves all {@link Rating} records for a given book.
     *
     * @param bookId the ID of the book whose ratings are fetched
     * @return a list of Rating records; empty list if none found or on error
     */
    @Override
    public List<Rating> findByBookId(Long bookId) {
        String sql = "SELECT * FROM ratings WHERE book_id = ?";
        logger.info("findByBookId() called for bookId={}", bookId);

        List<Rating> ratings = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, bookId);
            logger.debug("Executing SELECT: {} with bookId={}", sql, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rating rating = new Rating();
                    rating.setId(rs.getLong("id"));
                    rating.setUserId(rs.getLong("user_id"));
                    rating.setBookId(rs.getLong("book_id"));
                    rating.setScore(rs.getInt("score"));
                    ratings.add(rating);
                }
                logger.info("Fetched {} ratings for bookId={}", ratings.size(), bookId);
            }
        } catch (SQLException e) {
            logger.error("Database error in findByBookId() for bookId={}", bookId, e);
        }
        return ratings;
    }

    /**
     * Retrieves all {@link Rating} records for a given user.
     *
     * @param userId the ID of the user whose ratings are fetched
     * @return a list of Rating records; empty list if none found or on error
     */
    @Override
    public List<Rating> findByUserId(Long userId) {
        String sql = "SELECT * FROM ratings WHERE user_id = ?";
        logger.info("findByUserId() called for userId={}", userId);

        List<Rating> ratings = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            logger.debug("Executing SELECT: {} with userId={}", sql, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rating rating = new Rating();
                    rating.setId(rs.getLong("id"));
                    rating.setUserId(rs.getLong("user_id"));
                    rating.setBookId(rs.getLong("book_id"));
                    rating.setScore(rs.getInt("score"));
                    ratings.add(rating);
                }
                logger.info("Fetched {} ratings for userId={}", ratings.size(), userId);
            }
        } catch (SQLException e) {
            logger.error("Database error in findByUserId() for userId={}", userId, e);
        }
        return ratings;
    }
}
