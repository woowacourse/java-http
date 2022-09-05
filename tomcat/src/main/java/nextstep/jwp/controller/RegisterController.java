package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.DtoAssembler;
import nextstep.jwp.service.UserService;
import nextstep.jwp.servlet.handler.Controller;
import nextstep.jwp.servlet.handler.RequestMapping2;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse2;

@RequestMapping2(path = "/register")
public class RegisterController extends Controller {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    public void doGet(HttpRequest request, HttpResponse2 response) {
        response.ok().setViewResource("/register.html");
    }

    public void doPost(HttpRequest request, HttpResponse2 response) {
        userService.saveUser(DtoAssembler.ofSaveUserDto(request));
        response.redirect("/index.html");
    }
}
