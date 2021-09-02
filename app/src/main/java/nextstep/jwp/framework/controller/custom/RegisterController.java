package nextstep.jwp.framework.controller.custom;

import java.util.Map;
import nextstep.jwp.framework.controller.CustomController;
import nextstep.jwp.framework.controller.ResponseTemplate;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.web.application.UserService;

public class RegisterController extends CustomController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getMethod();
        String url = httpRequest.getUrl();
        return url.equals("/register") &&
            (httpMethod.equals(HttpMethod.GET) || httpMethod.equals(HttpMethod.POST));
    }

    protected HttpResponse doGet(HttpRequest httpRequest) {
        return ResponseTemplate.ok("/register.html").build();
    }

    protected HttpResponse doPost(HttpRequest httpRequest) {
        Map<String, String> attributes = httpRequest.getContentAsAttributes();
        try {
            userService.register(
                attributes.get("account"),
                attributes.get("password"),
                attributes.get("email")
            );
            return ResponseTemplate.redirect("/index.html").build();
        } catch (RuntimeException runtimeException) {
            return ResponseTemplate.unauthorize("/401.html").build();
        }
    }
}
