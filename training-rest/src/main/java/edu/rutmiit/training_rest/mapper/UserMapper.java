package edu.rutmiit.training_rest.mapper;

import edu.rutmiit.training_checker_api_contract.dto.UserResponse;
import edu.rutmiit.training_rest.domain.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .birthDate(entity.getBirthDate())
                .weightKg(entity.getWeightKg())
                .build();
    }
}