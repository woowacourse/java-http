package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.DtoAssembler;
import nextstep.jwp.service.UserService;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.request.HttpRequest;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        super("/register");
        this.userService = userService;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.ok().setViewResource("/register.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        userService.saveUser(DtoAssembler.ofSaveUserDto(request));
        response.redirect("/index.html");
    }
}
