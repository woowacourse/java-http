package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.DtoAssembler;
import nextstep.jwp.service.UserService;
import nextstep.jwp.servlet.handler.Controller;
import nextstep.jwp.servlet.handler.RequestMapping;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

@RequestMapping(path = "/register")
public class RegisterController extends Controller {

    private final UserService userService;

    public RegisterController(UserService userService) {
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
