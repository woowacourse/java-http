package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.DtoAssembler;
import nextstep.jwp.service.UserService;
import nextstep.jwp.servlet.handler.RequestMapping;
import org.apache.coyote.servlet.cookie.HttpCookie;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.ResponseEntity;
import org.apache.coyote.servlet.session.Session;
import org.apache.coyote.support.HttpMethod;

public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = HttpMethod.GET, path = {"/login", "/login.html"})
    public ResponseEntity getLoginPage(HttpRequest request) {
        final var session = request.getSession();
        if (session.hasAttribute(Session.USER_ATTRIBUTE)) {
            return ResponseEntity.redirect("/index.html").build();
        }
        return ResponseEntity.ok().setViewResource("/login.html").build();
    }

    @RequestMapping(method = HttpMethod.POST, path = "/login")
    public ResponseEntity login(HttpRequest request) {
        final var user = userService.login(DtoAssembler.ofLoginDto(request));
        final var session = request.getSession();
        session.setAttribute(Session.USER_ATTRIBUTE, user);
        return ResponseEntity.redirect("/index.html")
                .setCookie(HttpCookie.ofSession(session))
                .build();
    }

    @RequestMapping(method = HttpMethod.GET, path = "/register")
    public String getRegisterPage() {
        return "/register.html";
    }

    @RequestMapping(method = HttpMethod.POST, path = "/register")
    public ResponseEntity register(HttpRequest request) {
        userService.saveUser(DtoAssembler.ofSaveUserDto(request));
        return ResponseEntity.redirect("/index.html").build();
    }
}
