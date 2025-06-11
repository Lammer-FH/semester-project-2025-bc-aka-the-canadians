package com.campuslostfound.service;

import com.campuslostfound.model.User;
import com.campuslostfound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        // Additional validation for email uniqueness during updates
        if (user.getId() != null) {
            // This is an update - check if email is being changed to one that already
            // exists
            Optional<User> existingUserWithEmail = getUserByEmail(user.getEmail());
            if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Email address is already in use by another user");
            }
        } else {
            // This is a new user - check if email already exists
            if (existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("User with this email already exists");
            }
        }
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}