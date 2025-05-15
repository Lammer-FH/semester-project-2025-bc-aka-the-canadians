package com.campuslostfound.controller;

import com.campuslostfound.dto.UserDTO;
import com.campuslostfound.mapper.UserMapper;
import com.campuslostfound.model.User;
import com.campuslostfound.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;


import java.util.Optional;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Erstellt einen neuen Benutzer.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        // Prüfen, ob E-Mail-Adresse bereits existiert
        if (userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User with email " + userDTO.getEmail() + " already exists");
        }
        
        // Konvertierung und Speichern
        User user = userMapper.toEntity(userDTO);
        User savedUser = userService.saveUser(user);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toDTO(savedUser));
    }

    /**
     * Gibt einen Benutzer anhand seiner ID zurück.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> ResponseEntity.ok(userMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Aktualisiert einen bestehenden Benutzer.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        // Prüfen, ob Benutzer existiert
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Prüfen, ob die E-Mail bereits bei einem anderen Benutzer verwendet wird
        Optional<User> existingUserWithEmail = userService.getUserByEmail(userDTO.getEmail());
        if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        // Setzen der ID und Speichern
        userDTO.setId(id);
        User updatedUser = userService.saveUser(userMapper.toEntity(userDTO));
        
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }
} 