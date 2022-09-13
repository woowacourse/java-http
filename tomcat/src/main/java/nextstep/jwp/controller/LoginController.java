package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.DtoAssembler;
import nextstep.jwp.service.UserService;
import nextstep.jwp.view.Page;
import org.apache.catalina.session.Session;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController(UserService userService) {
        super("/login");
        this.userService = userService;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        final var session = request.getSession();
        if (session.hasAttribute(Session.USER_ATTRIBUTE)) {
            response.redirect(Page.INDEX.getUri());
            return;
        }
        response.ok().setViewResource(Page.LOGIN.getUri());
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        final var user = userService.login(DtoAssembler.ofLoginDto(request));
        final var session = request.getSession();
        session.setAttribute(Session.USER_ATTRIBUTE, user);
        response.redirect(Page.INDEX.getUri());
    }
}
