package com.bank.controller;

import com.bank.model.User;
import com.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.upload.dir}")
    private String uploadDir;

    // Get profile
    @GetMapping
    public ResponseEntity<?> getProfile(
            Authentication auth) {
        try {
            User user = userRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException(
                        "User not found"));

            Map<String, Object> profile =
                new HashMap<>();
            profile.put("id", user.getId());
            profile.put("fullName", user.getFullName());
            profile.put("email", user.getEmail());
            profile.put("phone", user.getPhone());
            profile.put("address", user.getAddress());
            profile.put("role", user.getRole());
            profile.put("createdAt", user.getCreatedAt());

            // Check if photo exists
            String photoPath = uploadDir + "/" +
                user.getId() + ".jpg";
            File photoFile = new File(photoPath);
            if (photoFile.exists()) {
                profile.put("hasPhoto", true);
                profile.put("photoUrl",
                    "/api/profile/photo/" +
                    user.getId());
            } else {
                profile.put("hasPhoto", false);
                profile.put("photoUrl", null);
            }

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    // Upload profile photo
    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadPhoto(
            Authentication auth,
            @RequestParam("photo")
            MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("Please select a photo!");
            }

            // Check file type
            String contentType =
                file.getContentType();
            if (contentType == null ||
                !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body("Only image files allowed!");
            }

            // Check file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body("File too large! Max 5MB.");
            }

            User user = userRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException(
                        "User not found"));

            // Create upload directory
            Path uploadPath =
                Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save file with user id as name
            String filename = user.getId() + ".jpg";
            Path filePath =
                uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            return ResponseEntity.ok(
                "Photo uploaded successfully!");

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    // Get profile photo
    @GetMapping("/photo/{userId}")
    public ResponseEntity<byte[]> getPhoto(
            @PathVariable Long userId) {
        try {
            String photoPath = uploadDir +
                "/" + userId + ".jpg";
            File photoFile = new File(photoPath);

            if (!photoFile.exists()) {
                return ResponseEntity.notFound()
                    .build();
            }

            byte[] imageBytes =
                Files.readAllBytes(
                    photoFile.toPath());

            return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(imageBytes);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .build();
        }
    }

    // Update profile
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(
            Authentication auth,
            @RequestBody Map<String, String> request) {
        try {
            User user = userRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException(
                        "User not found"));

            if (request.containsKey("fullName") &&
                !request.get("fullName").isEmpty()) {
                user.setFullName(
                    request.get("fullName"));
            }
            if (request.containsKey("phone")) {
                user.setPhone(request.get("phone"));
            }
            if (request.containsKey("address")) {
                user.setAddress(
                    request.get("address"));
            }

            userRepository.save(user);
            return ResponseEntity.ok(
                "Profile updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    // Change password
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication auth,
            @RequestBody Map<String, String> request) {
        try {
            User user = userRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException(
                        "User not found"));

            String currentPassword =
                request.get("currentPassword");
            String newPassword =
                request.get("newPassword");
            String confirmPassword =
                request.get("confirmPassword");

            if (!passwordEncoder.matches(
                    currentPassword,
                    user.getPassword())) {
                return ResponseEntity.badRequest()
                    .body("Current password is wrong!");
            }

            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest()
                    .body("Passwords do not match!");
            }

            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                    .body("Min 6 characters required!");
            }

            user.setPassword(
                passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return ResponseEntity.ok(
                "Password changed successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }
}