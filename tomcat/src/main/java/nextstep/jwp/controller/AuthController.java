package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import nextstep.jwp.servlet.handler.RequestMapping;
import org.apache.coyote.servlet.cookie.HttpCookie;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.request.Parameters;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.servlet.response.HttpResponse.HttpResponseBuilder;
import org.apache.coyote.servlet.session.SessionRepository;
import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.support.HttpStatus;

public class AuthController {

    private final UserService userService;
    private final SessionRepository sessionRepository;

    public AuthController(UserService userService, SessionRepository sessionRepository) {
        this.userService = userService;
        this.sessionRepository = sessionRepository;
    }

    @RequestMapping(method = HttpMethod.GET, path = "/login")
    public String getLoginPage() {
        return "/login.html";
    }

    @RequestMapping(method = HttpMethod.POST, path = "/login")
    public HttpResponse login(HttpRequest request) {
        Parameters parameters = request.getParameters();
        final var account = parameters.get("account");
        final var password = parameters.get("password");

        final var user = userService.login(account, password);
        final var sessionId = sessionRepository.generateNewSession(user.getId());
        final var sessionCookie = HttpCookie.ofSessionId(sessionId);
        return new HttpResponseBuilder(HttpStatus.FOUND)
                .setLocation("/index.html")
                .setCookie(sessionCookie)
                .build();
    }

    @RequestMapping(method = HttpMethod.GET, path = "/register")
    public String getRegisterPage() {
        return "/register.html";
    }

    @RequestMapping(method = HttpMethod.POST, path = "/register")
    public HttpResponse register(HttpRequest request) {
        Parameters parameters = request.getParameters();
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");
        userService.saveUser(account, password, email);
        return new HttpResponseBuilder(HttpStatus.FOUND)
                .setLocation("/index.html")
                .build();
    }
}
