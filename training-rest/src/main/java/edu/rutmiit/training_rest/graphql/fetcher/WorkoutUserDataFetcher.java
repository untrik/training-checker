package edu.rutmiit.training_rest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import edu.rutmiit.training_checker_api_contract.dto.UserResponse;
import edu.rutmiit.training_checker_api_contract.dto.WorkoutResponse;
import edu.rutmiit.training_rest.service.UserService;

@DgsComponent
public class WorkoutUserDataFetcher {

    private final UserService userService;


    public WorkoutUserDataFetcher(UserService userService) {
        this.userService = userService;
    }

    @DgsData(parentType = "Workout", field = "user")
    public UserResponse user(DgsDataFetchingEnvironment dfe){
        WorkoutResponse workout = dfe.getSource();
        if (workout.getUser() != null) {
            return workout.getUser();
        }
        return null;
    }
}
