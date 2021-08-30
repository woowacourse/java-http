package nextstep.jwp.dashboard.controller;

import java.util.Map;

import nextstep.jwp.dashboard.service.UserService;
import nextstep.jwp.httpserver.controller.AbstractController;
import nextstep.jwp.httpserver.domain.StatusCode;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest, Map<String, String> param) {
        return new HttpResponse.Builder()
                .statusCode(StatusCode.OK)
                .build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest, Map<String, String> param) {
        final String account = param.get("account");
        final String password = param.get("password");
        final String email = param.get("email");
        userService.join(account, password, email);
        return new HttpResponse.Builder()
                .statusCode(StatusCode.FOUND)
                .header("Location", "/index.html")
                .build();
    }
}
