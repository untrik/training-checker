package edu.rutmiit.training_rest.service;

import edu.rutmiit.training_checker_api_contract.dto.*;
import edu.rutmiit.training_checker_api_contract.exception.ResourceNotFoundException;
import edu.rutmiit.training_rest.domain.UserEntity;
import edu.rutmiit.training_rest.mapper.UserMapper;
import edu.rutmiit.training_rest.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@PreAuthorize("hasAnyRole('USER', 'OPERATOR')")
public class UserService {
    private final WorkoutService workoutService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(@Lazy WorkoutService workoutService, UserRepository userRepository, UserMapper userMapper) {
        this.workoutService = workoutService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Transactional(readOnly = true)
    public UserResponse findById(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.email().trim())) {
            throw new DataIntegrityViolationException("User with this email already exists");
        }
        UserEntity user = userRepository.save(
                new UserEntity(UUID.randomUUID(), request.username().trim(), request.email().trim(),
                        request.birthDate(), request.weightKg())
        );
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        UserEntity existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        existing.setUsername(request.username().trim());
        existing.setEmail(request.email().trim());
        existing.setBirthDate(request.birthDate());
        existing.setWeightKg(request.weightKg());
        return userMapper.toResponse(existing);
    }

    @Transactional
    public UserResponse patchUser(UUID id, PatchUserRequest request) {
        UserEntity existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        existing.setUsername(request.username() != null ? request.username().trim() : existing.getUsername());
        existing.setEmail(request.email() != null ? request.email().trim() : existing.getEmail());
        existing.setBirthDate(request.birthDate() != null ? request.birthDate() : existing.getBirthDate());
        existing.setWeightKg(request.weightKg() != null ? request.weightKg() : existing.getWeightKg());
        return userMapper.toResponse(existing);
    }

    @Transactional
    @PreAuthorize("hasRole('OPERATOR')")
    public UserResponse delete(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        UserResponse userResponse = userMapper.toResponse(user);
        workoutService.deleteWorkoutsByUserId(id);
        userRepository.delete(user);
        return userResponse;
    }

}
