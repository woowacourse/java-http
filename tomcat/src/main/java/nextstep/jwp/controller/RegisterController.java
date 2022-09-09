package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.DtoAssembler;
import nextstep.jwp.service.UserService;
import nextstep.jwp.view.Page;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        super("/register");
        this.userService = userService;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.ok().setViewResource(Page.REGISTER.getUri());
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        userService.saveUser(DtoAssembler.ofSaveUserDto(request));
        response.redirect(Page.INDEX.getUri());
    }
}
