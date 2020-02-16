package nexters.moss.server.web;

import nexters.moss.server.application.CakeApplicationService;
import nexters.moss.server.application.dto.cake.*;
import nexters.moss.server.application.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cake")
public class CakeController {
    private CakeApplicationService cakeApplicationService;

    public CakeController(CakeApplicationService cakeApplicationService) {
        this.cakeApplicationService = cakeApplicationService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Response<Long> createNewCake(@RequestBody CreateNewCakeRequest createNewCakeRequest) {
        return cakeApplicationService.createNewCake(createNewCakeRequest);
    }

    @GetMapping("")
    @ResponseStatus(value = HttpStatus.OK)
    public Response<NewCakeDTO> getNewCake(@RequestParam Long userId, @RequestParam Long categoryId) {
        return cakeApplicationService.getNewCake(userId, categoryId);
    }
}
