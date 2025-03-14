package com.usermgmt.usermgmtsystem.user;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users") // Defines the base URL for user-related endpoints
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves a list of all users.
     *
     * @return List of users from the database.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity containing the user if found, otherwise an error
     *         message.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Creates a new user.
     *
     * @param user The user object to be saved.
     * @return ResponseEntity with the created user and HTTP status.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            userRepository.saveAndFlush(user);

            Map<String, User> response = new HashMap<>();
            response.put("user", user);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Updates an existing user's details.
     *
     * @param id   The ID of the user to update.
     * @param user The updated user details.
     * @return ResponseEntity with the updated user or an error message.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            // Fetch the existing user by ID
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

            // Update user fields
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setAge(user.getAge());
            existingUser.setPosition(user.getPosition());
            existingUser.setSalary(user.getSalary());

            // Update file URL if present
            if (user.getFileURL() != null) {
                existingUser.setFileURL(user.getFileURL());
            }

            // Save the updated user
            User updatedUser = userRepository.saveAndFlush(existingUser);

            Map<String, User> response = new HashMap<>();
            response.put("user", updatedUser);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (OptimisticLockingFailureException e) {
            // Handle concurrent modification error
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "This record was modified by another user. Please refresh and try again.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @return ResponseEntity confirming the deletion or an error message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            // Fetch user to retrieve name for confirmation message
            User userToBeDeleted = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            String name = userToBeDeleted.getName();

            // Delete the user
            userRepository.deleteById(id);

            // Return success message
            Map<String, String> response = new HashMap<>();
            response.put("message", "User " + name + " has been successfully deleted");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
