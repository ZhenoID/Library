package epam.finalProject.service;

import epam.finalProject.DAO.UserDao;
import epam.finalProject.DAO.UserDaoImpl;
import epam.finalProject.entity.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for {@link User} operations.
 * Provides methods for user registration, authentication, role update, password update,
 * retrieval, and deletion.
 * Delegates database operations to {@link UserDao}.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDAO;

    /**
     * Constructs a UserServiceImpl using the default {@link UserDaoImpl}.
     */
    public UserServiceImpl() {
        this(new UserDaoImpl());
        logger.info("UserServiceImpl initialized with default UserDaoImpl");
    }

    /**
     * Constructs a UserServiceImpl using the specified {@link UserDao}.
     *
     * @param userDAO the DAO to delegate user-related operations to
     */
    public UserServiceImpl(UserDao userDAO) {
        this.userDAO = userDAO;
        logger.info("UserServiceImpl initialized with provided UserDao");
    }

    /**
     * Registers a new {@link User}. Checks for existing username, hashes the password,
     * sets role to "USER", and saves to database.
     *
     * @param user the User object containing username and plaintext password
     * @return {@code true} if registration succeeded, {@code false} if username already exists or save failed
     */
    @Override
    public boolean register(User user) {
        logger.info("register() called for username='{}'", user.getUsername());
        if (userDAO.findByUsername(user.getUsername()) != null) {
            logger.warn("Registration failed: username '{}' already exists", user.getUsername());
            return false;
        }
        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashed);
        user.setRole("USER");
        boolean result = userDAO.save(user);
        if (result) {
            logger.info("User registered successfully: username='{}'", user.getUsername());
        } else {
            logger.error("User registration failed for username='{}'", user.getUsername());
        }
        return result;
    }

    /**
     * Retrieves a {@link User} by username.
     *
     * @param username the username to search for
     * @return the User if found, or {@code null} otherwise
     */
    @Override
    public User getByUsername(String username) {
        logger.info("getByUsername() called for username='{}'", username);
        User user = userDAO.findByUsername(username);
        if (user != null) {
            logger.debug("User found: id={} username='{}'", user.getId(), user.getUsername());
        } else {
            logger.warn("No user found for username='{}'", username);
        }
        return user;
    }

    /**
     * Updates the password of an existing {@link User}.
     * Assumes the password field on the User object is already hashed.
     *
     * @param user the User object containing username and new hashed password
     * @return {@code true} if the password was updated, {@code false} otherwise
     */
    @Override
    public boolean updatePassword(User user) {
        logger.info("updatePassword() called for username='{}'", user.getUsername());
        boolean result = userDAO.updatePassword(user);
        if (result) {
            logger.info("Password updated successfully for username='{}'", user.getUsername());
        } else {
            logger.error("Password update failed for username='{}'", user.getUsername());
        }
        return result;
    }

    /**
     * Updates the role of a {@link User} identified by userId.
     *
     * @param userId  the ID of the user to update
     * @param newRole the new role to set
     * @return {@code true} if the role was updated, {@code false} otherwise
     */
    @Override
    public boolean updateRole(Long userId, String newRole) {
        logger.info("updateRole() called for userId={} newRole='{}'", userId, newRole);
        boolean result = userDAO.updateRole(userId, newRole);
        if (result) {
            logger.info("Role updated successfully for userId={} to '{}'", userId, newRole);
        } else {
            logger.error("Role update failed for userId={}", userId);
        }
        return result;
    }

    /**
     * Retrieves all {@link User} records from the database.
     *
     * @return a List of all Users; empty if none found or on error
     */
    @Override
    public Page<User> findAll(Pageable pageable) {
        long total = userDAO.count();
        List<User> list = userDAO.findAll(pageable);
        return new PageImpl<>(list, pageable, total);
    }

    /**
     * Deletes the specified {@link User}.
     *
     * @param user the User to delete (must have a valid ID)
     * @return {@code true} if the user was deleted, {@code false} otherwise
     */
    @Override
    public boolean deleteUser(User user) {
        logger.info("deleteUser() called for userId={} username='{}'", user.getId(), user.getUsername());
        boolean result = userDAO.delete(user);
        if (result) {
            logger.info("User deleted successfully: userId={}", user.getId());
        } else {
            logger.error("User deletion failed for userId={}", user.getId());
        }
        return result;
    }

    /**
     * Retrieves a {@link User} by its ID.
     *
     * @param id the ID of the user to retrieve
     * @return the User if found, or {@code null} otherwise
     */
    @Override
    public User getById(Long id) {
        logger.info("getById() called for userId={}", id);
        User user = userDAO.findById(id);
        if (user != null) {
            logger.debug("User found: id={} username='{}'", user.getId(), user.getUsername());
        } else {
            logger.warn("No user found for userId={}", id);
        }
        return user;
    }

    /**
     * Authenticates a user by verifying the provided plaintext password against the stored hash.
     *
     * @param username the username of the user attempting to authenticate
     * @param password the plaintext password provided
     * @return {@code true} if authentication succeeds, {@code false} otherwise
     */
    @Override
    public boolean authenticate(String username, String password) {
        logger.info("authenticate() called for username='{}'", username);
        User user = userDAO.findByUsername(username);
        if (user == null) {
            logger.warn("Authentication failed: user '{}' not found", username);
            return false;
        }
        boolean matches = BCrypt.checkpw(password, user.getPassword());
        if (matches) {
            logger.info("Authentication successful for username='{}'", username);
        } else {
            logger.warn("Authentication failed: invalid password for username='{}'", username);
        }
        return matches;
    }
}
