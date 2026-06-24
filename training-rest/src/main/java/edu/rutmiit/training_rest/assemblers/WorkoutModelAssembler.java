package edu.rutmiit.training_rest.assemblers;

import edu.rutmiit.training_checker_api_contract.dto.UserResponse;
import edu.rutmiit.training_checker_api_contract.dto.WorkoutResponse;
import edu.rutmiit.training_rest.controllers.WorkoutController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class WorkoutModelAssembler implements RepresentationModelAssembler<WorkoutResponse, EntityModel<WorkoutResponse>> {
    @Override
    public EntityModel<WorkoutResponse> toModel(WorkoutResponse workout) {
        EntityModel<WorkoutResponse> model = EntityModel.of(workout,
                linkTo(methodOn(WorkoutController.class).getWorkoutById(workout.getId())).withSelfRel()
        );
        if (workout.getFinishedAt() == null) {
            model.add(linkTo(methodOn(WorkoutController.class)
                    .createMetric(workout.getId(), null)).withRel("createMetric"));
            model.add(linkTo(methodOn(WorkoutController.class)
                    .finishWorkout(workout.getId()/*, null */)).withRel("finish"));
        } else {
            model.add(linkTo(methodOn(WorkoutController.class)
                    .deleteWorkout(workout.getId())).withRel("delete"));
            model.add(linkTo(methodOn(WorkoutController.class)
                    .getMetricsByWorkoutId(workout.getId())).withRel("metrics"));
            model.add(linkTo(methodOn(WorkoutController.class).getSummaryByWorkoutId(workout.getId())).withRel("summary"));
        }
        return model;
    }
}
