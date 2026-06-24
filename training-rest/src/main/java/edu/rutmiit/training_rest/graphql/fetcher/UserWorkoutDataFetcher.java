package edu.rutmiit.training_rest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.training_checker_api_contract.dto.PagedResponse;
import edu.rutmiit.training_checker_api_contract.dto.UserResponse;
import edu.rutmiit.training_checker_api_contract.dto.WorkoutResponse;
import edu.rutmiit.training_checker_api_contract.dto.WorkoutType;
import edu.rutmiit.training_rest.graphql.types.PageInfoGql;
import edu.rutmiit.training_rest.graphql.types.WorkoutConnectionGql;
import edu.rutmiit.training_rest.graphql.types.WorkoutFilterGql;
import edu.rutmiit.training_rest.service.WorkoutService;

@DgsComponent
public class UserWorkoutDataFetcher {

    private final WorkoutService workoutService;

    public UserWorkoutDataFetcher(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @DgsData(parentType = "User", field = "workouts")
    public WorkoutConnectionGql workouts(
            DgsDataFetchingEnvironment dfe,
            @InputArgument WorkoutFilterGql filter,
            @InputArgument Integer page,
            @InputArgument Integer size) {
        UserResponse user = dfe.getSource();
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        WorkoutType type = filter != null ? filter.type() : null;
        Double totalDistanceKm = filter != null ? filter.totalDistanceKm() : null;
        Integer totalCalories = filter != null ? filter.totalCalories() : null;
        String titleSearch = filter != null ? filter.titleSearch() : null;
        PagedResponse<WorkoutResponse> paged = workoutService.getWorkoutsByUser(user.getId(),
                type, totalDistanceKm, totalCalories,
                titleSearch, pageNum,pageSize);
        return new WorkoutConnectionGql(
                paged.content(),
                new PageInfoGql(paged.pageNumber(), paged.pageSize(), paged.totalPages(), paged.last()),
                (int) paged.totalElements());
    }
}
