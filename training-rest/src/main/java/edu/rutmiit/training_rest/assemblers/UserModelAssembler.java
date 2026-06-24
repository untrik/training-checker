package edu.rutmiit.training_rest.assemblers;

import edu.rutmiit.training_checker_api_contract.dto.UserResponse;
import edu.rutmiit.training_rest.controllers.UserController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(UserResponse user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).patchUser(user.getId(), null)).withRel("patch"),
                linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).getWorkoutsByUser(user.getId(),null,null,null ,null,0,20)).withRel("workouts"),
                linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete")
        );
    }
}
