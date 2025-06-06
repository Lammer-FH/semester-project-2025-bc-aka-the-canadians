package com.campuslostfound.service;

import com.campuslostfound.model.User;
import com.campuslostfound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User createUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        // Check if email is being changed and if it's already taken
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email is already taken");
        }

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }
} 