package nexters.moss.server.web;

import nexters.moss.server.application.UserApplicationService;
import nexters.moss.server.application.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {
    private UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @PostMapping("")
    @ResponseStatus(value = HttpStatus.OK)
    public Response<Long> join(
            @RequestHeader String accessToken,
            @RequestBody String nickname
    ) {
        return userApplicationService.join(accessToken, nickname);
    }
}
