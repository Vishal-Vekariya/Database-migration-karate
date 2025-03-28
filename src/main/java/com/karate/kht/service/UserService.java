package com.karate.kht.service;

import com.karate.kht.entity.UserEntity;
import com.karate.kht.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    public UserEntity getUserById(Long id) {
        return userRepository
                .findById(id)
                .orElse(null);
    }

    public UserEntity createUser(UserEntity userEntity) {
        UserEntity newUser = new UserEntity();

        newUser.setUsername(userEntity.getUsername());
        newUser.setFirstName(userEntity.getFirstName());
        newUser.setLastName(userEntity.getLastName());
        newUser.setEmail(userEntity.getEmail());
        newUser.setPassword(encoder.encode(userEntity.getPassword()));
        newUser.setRole(userEntity.getRole());
        newUser.setKarateOrganization(userEntity.getKarateOrganization());

        return userRepository.save(newUser);
    }


    public UserEntity updateUser(Long id, UserEntity userEntity) {
        UserEntity existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));

        if (isValid(userEntity.getFirstName()))
            existingUser.setFirstName(userEntity.getFirstName());

        if (isValid(userEntity.getLastName()))
            existingUser.setLastName(userEntity.getLastName());

        if (isValid(userEntity.getEmail()))
            existingUser.setEmail(userEntity.getEmail());

        if (isValid(userEntity.getPassword()))
            existingUser.setPassword(encoder.encode(userEntity.getPassword()));

        if (isValid(userEntity.getUsername()))
            existingUser.setUsername(userEntity.getUsername());

        if (isValid(userEntity.getKarateOrganization()))
            existingUser.setKarateOrganization(userEntity.getKarateOrganization());

        // verify role
        if (userEntity.getRole() != null) {
            existingUser.setRole(userEntity.getRole());
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    public Long getUserCount() {
        return userRepository.count();
    }

    // Utility method to check if a string is not null and not empty
    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
