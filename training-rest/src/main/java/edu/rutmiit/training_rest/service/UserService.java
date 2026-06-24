package edu.rutmiit.training_rest.service;

import edu.rutmiit.training_checker_api_contract.dto.*;
import edu.rutmiit.training_checker_api_contract.exception.ResourceNotFoundException;
import edu.rutmiit.training_rest.graphql.types.UpdateUserInputGql;
import edu.rutmiit.training_rest.storage.InMemoryStorage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final InMemoryStorage storage;
    private final WorkoutService  workoutService;

    public UserService(InMemoryStorage storage, @Lazy WorkoutService workoutService) {
        this.storage = storage;
        this.workoutService = workoutService;
    }

    public UserResponse findById(Long userId) {
        return Optional.ofNullable(storage.users.get(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    public UserResponse createUser(UserRequest request) {
        long id = storage.userSequence.incrementAndGet();
        UserResponse response = UserResponse.builder()
                .id(id)
                .username(request.username())
                .email(request.email())
                .birthDate(request.birthDate())
                .weightKg(request.weightKg())
                .build();
        storage.users.put(id, response);
        return response;
    }
    public UserResponse updateUser(Long id, UpdateUserRequest request){
        UserResponse existing = findById(id);
        UserResponse updated = UserResponse.builder()
                .id(id)
                .username(request.username())
                .email(request.email())
                .birthDate(request.birthDate())
                .weightKg(request.weightKg())
                .build();
        storage.users.put(id, updated);
        return updated;
    }
    public UserResponse patchUser(Long id, PatchUserRequest request) {
        UserResponse existing = findById(id);
        UserResponse updated = UserResponse.builder()
                .id(id)
                .username(request.username() != null ? request.username() : existing.getUsername())
                .email(request.email() != null ? request.username() : existing.getEmail())
                .birthDate(request.birthDate() != null ? request.birthDate() : existing.getBirthDate())
                .weightKg(request.weightKg() != null ? request.weightKg() : existing.getWeightKg())
                .build();
        storage.users.put(id, updated);
        return updated;
    }

    public UserResponse delete(Long id){
        UserResponse user = findById(id);
        workoutService.deleteWorkoutsByUserId(id);
        storage.users.remove(id);
        return user;
    }



}
