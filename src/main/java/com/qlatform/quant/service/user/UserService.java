package com.qlatform.quant.service.user;

import com.qlatform.quant.exception.UserAlreadyExistsException;
import com.qlatform.quant.model.User;
import com.qlatform.quant.repository.userdb.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm);
    }

    public void toggleUserEnabled(String userId, boolean enabled) {
        userRepository.updateEnabledStatus(userId, enabled);
    }

    public List<User> findUnverifiedUsers() {
        return userRepository.findByEmailVerifiedFalse();
    }
}