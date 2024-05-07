package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<User> findAllUsers (Pageable pageable) {
       return userRepository.findAll(pageable);
    }

    public User findById (Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User findByUserName (String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User findByEmail (String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


}
