//package epam.finalProject.controller;
//
//import epam.finalProject.entity.User;
//import epam.finalProject.service.UserService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
///**
// * Controller for managing users.
// * Provides endpoints to list all users and delete a specific user.
// */
//@Controller
//@RequestMapping("/users")
//public class UserController {
//
//    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
//
//    @Autowired
//    private UserService userService;
//
//    /**
//     * Handles GET requests to "/users".
//     * Retrieves all users from the service and adds them to the model.
//     *
//     * @param model the {@code Model} to which the list of users will be added
//     * @return the name of the Thymeleaf template for displaying all users
//     */
//    @GetMapping
//    public String listUsers(Model model) {
//        logger.info("GET /users - fetching list of all users");
//        model.addAttribute("users", userService.findAll());
//        logger.debug("Number of users fetched: {}", userService.findAll().size());
//        return "admin/users";
//    }
//
//    /**
//     * Handles POST requests to "/users/delete/{id}".
//     * Deletes the user with the given ID if they exist.
//     *
//     * @param id the ID of the user to delete
//     * @return redirect to "/admin/users" after deletion
//     */
//    @PostMapping("/delete/{id}")
//    public String deleteUser(@PathVariable Long id) {
//        logger.info("POST /users/delete/{} - attempting to delete user", id);
//        User user = userService.getById(id);
//        if (user != null) {
//            userService.deleteUser(user);
//            logger.info("User deleted successfully: id={}", id);
//        } else {
//            logger.warn("User with id={} not found. No deletion performed.", id);
//        }
//        return "redirect:/admin/users";
//    }
//}
