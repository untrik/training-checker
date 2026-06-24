package edu.rutmiit.training_rest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.training_checker_api_contract.dto.PatchUserRequest;
import edu.rutmiit.training_checker_api_contract.dto.UpdateUserRequest;
import edu.rutmiit.training_checker_api_contract.dto.UserRequest;
import edu.rutmiit.training_checker_api_contract.dto.UserResponse;
import edu.rutmiit.training_rest.graphql.types.CreateUserInputGql;
import edu.rutmiit.training_rest.graphql.types.UpdateUserInputGql;
import edu.rutmiit.training_rest.service.UserService;
import org.springframework.data.jpa.repository.Query;

@DgsComponent
public class UserDataFetcher {
    private final UserService userService;

    public UserDataFetcher(UserService userService) {
        this.userService = userService;
    }

    @DgsQuery
    public UserResponse user(@InputArgument String id){
        return userService.findById(Long.parseLong(id));
    }

    @DgsMutation
    public UserResponse createUser(@InputArgument CreateUserInputGql input) {
        UserRequest request = new UserRequest(
                input.username(),
                input.email(),
                input.birthDate(),
                input.weightKg()
        );
        return userService.createUser(request);
    }
    @DgsMutation
    public UserResponse updateUser(@InputArgument String id,@InputArgument UpdateUserInputGql input){
        UpdateUserRequest request = new UpdateUserRequest(
                input.username(),
                input.email(),
                input.birthDate(),
                input.weightKg()
        );
        return userService.updateUser(Long.parseLong(id),request);
    }

    @DgsMutation
    public boolean deleteUser(@InputArgument String id) {
        userService.delete(Long.parseLong(id));
        return true;
    }
}
