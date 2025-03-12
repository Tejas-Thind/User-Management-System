package com.usermgmt.usermgmtsystem.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

import com.usermgmt.usermgmtsystem.service.FileService;
import com.usermgmt.usermgmtsystem.user.UserRepository;
import com.usermgmt.usermgmtsystem.user.User;

// The @RestController annotation tells Spring this class will handle HTTP requests
@RestController
@RequestMapping("/api/users/file")
public class UploadFileController {

    // Injects an instance of FileService to handle file upload logic
    @Autowired
    public FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{userId}/upload")
    public ResponseEntity<?> uploadFile(@PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {

        try {
            // First check if user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            String publicUrl = fileService.uploadFile(file);

            // Update user with file URL
            user.setFileURL(publicUrl);
            User savedUser = userRepository.saveAndFlush(user);

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("user", savedUser);
            response.put("message", "File uploaded successfully");
            response.put("fileUrl", publicUrl);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {

            e.printStackTrace();

            // Detailed error response
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "File upload failed: " + e.getMessage());
            errorResponse.put("errorType", e.getClass().getSimpleName());

            // If there's a cause, include it
            if (e.getCause() != null) {
                errorResponse.put("cause", e.getCause().getMessage());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}