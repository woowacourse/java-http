package nextstep.jwp.dashboard.controller;

import java.util.Map;

import nextstep.jwp.dashboard.controller.dto.UserDto;
import nextstep.jwp.dashboard.service.UserService;
import nextstep.jwp.httpserver.controller.AbstractController;
import nextstep.jwp.httpserver.domain.StatusCode;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;

    public LoginController(UserService userService) {
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
        String account = param.get("account");
        String password = param.get("password");
        UserDto userDto = userService.login(account, password);
        log.debug(userDto.toString());
        return new HttpResponse.Builder()
                .statusCode(StatusCode.FOUND)
                .header("Location", "/index.html")
                .build();
    }
}
