package edu.rutmiit.training_rest.controllers;

import edu.rutmiit.training_checker_api_contract.dto.*;
import edu.rutmiit.training_checker_api_contract.endpoints.UserApi;
import edu.rutmiit.training_rest.assemblers.UserModelAssembler;
import edu.rutmiit.training_rest.assemblers.WorkoutModelAssembler;
import edu.rutmiit.training_rest.service.UserService;
import edu.rutmiit.training_rest.service.WorkoutService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {
    private final UserService userService;
    private final WorkoutService workoutService;
    private final UserModelAssembler userModelAssembler;
    private final WorkoutModelAssembler workoutModelAssembler;
    private final PagedResourcesAssembler<WorkoutResponse> pagedWorkoutsAssembler;

    public UserController(UserService userService,
                          WorkoutService workoutService,
                          UserModelAssembler userModelAssembler,
                          WorkoutModelAssembler workoutModelAssembler,
                          PagedResourcesAssembler<WorkoutResponse> pagedWorkoutsAssembler) {
        this.userService = userService;
        this.workoutService = workoutService;
        this.userModelAssembler = userModelAssembler;
        this.workoutModelAssembler = workoutModelAssembler;
        this.pagedWorkoutsAssembler = pagedWorkoutsAssembler;
    }


    @Override
    public EntityModel<UserResponse> getUserById(Long id) {
        return userModelAssembler.toModel(userService.findById(id));
    }

    @Override
    public ResponseEntity<EntityModel<UserResponse>> createUser(UserRequest request) {
        UserResponse created = userService.createUser(request);
        EntityModel<UserResponse> model = userModelAssembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public EntityModel<UserResponse> patchUser(Long id, PatchUserRequest request) {
        return userModelAssembler.toModel(userService.patchUser(id, request));
    }

    @Override
    public EntityModel<UserResponse> updateUser(Long id, UpdateUserRequest request) {
        return userModelAssembler.toModel(userService.updateUser(id,request));
    }

    @Override
    public EntityModel<UserResponse> deleteUser(Long id) {
        UserResponse response = userService.findById(id);
        userService.delete(id);
        return userModelAssembler.toModel(response);
    }

    @Override
    public PagedModel<EntityModel<WorkoutResponse>> getWorkoutsByUser(Long id,WorkoutType type,Double totalDistanceKm,Integer totalCalories,String titleSearch, int page, int size) {
        userService.findById(id);
        PagedResponse<WorkoutResponse> paged = workoutService.getWorkoutsByUser(id,type,totalDistanceKm,totalCalories,titleSearch, page, size);
        Page<WorkoutResponse> springPage = new PageImpl<>(
                paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()),
                paged.totalElements()
        );
        return pagedWorkoutsAssembler.toModel(springPage, workoutModelAssembler);
    }
}
