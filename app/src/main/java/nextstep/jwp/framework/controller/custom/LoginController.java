package nextstep.jwp.framework.controller.custom;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.framework.controller.CustomController;
import nextstep.jwp.framework.controller.ResponseTemplate;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.session.HttpSession;
import nextstep.jwp.framework.infrastructure.http.session.HttpSessions;
import nextstep.jwp.framework.infrastructure.random.RandomIdGenerator;
import nextstep.jwp.web.application.UserService;
import nextstep.jwp.web.domain.User;

public class LoginController extends CustomController {

    private final UserService userService;
    private final RandomIdGenerator randomIdGenerator;

    public LoginController(UserService userService, RandomIdGenerator randomIdGenerator) {
        this.userService = userService;
        this.randomIdGenerator = randomIdGenerator;
    }

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getMethod();
        String url = httpRequest.getUrl();
        return url.equals("/login") &&
            (httpMethod.equals(HttpMethod.GET) || httpMethod.equals(HttpMethod.POST));
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        HttpSession httpSession = HttpSessions.getSession(httpRequest);
        if (Objects.isNull(httpSession) || Objects.isNull(httpSession.getAttribute("user"))) {
            return ResponseTemplate.ok("/login.html").build();
        }
        return ResponseTemplate.redirect("/index.html").build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        Map<String, String> attributes = httpRequest.getContentAsAttributes();
        String uuid = randomIdGenerator.generateId();
        try {
            User login = userService.login(attributes.get("account"), attributes.get("password"));
            HttpSessions.addSession(uuid);
            HttpSessions.getSession(uuid).setAttribute("user", login);
            return ResponseTemplate.redirect("/index.html")
                .cookie("JSESSIONID", uuid)
                .build();
        } catch (RuntimeException runtimeException) {
            return ResponseTemplate.unauthorize("/401.html").build();
        }
    }
}
