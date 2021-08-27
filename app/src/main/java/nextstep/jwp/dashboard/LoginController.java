package nextstep.jwp.dashboard;

import java.util.Map;

import nextstep.jwp.httpserver.controller.Handler;
import nextstep.jwp.httpserver.domain.StatusCode;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public HttpResponse handle(Map<String, String> param) {
        String account = param.get("account");
        String password = param.get("password");
        UserDto userDto = userService.login(account, password);
        log.debug(userDto.toString());
        return new HttpResponse.Builder()
                .statusCode(StatusCode.CREATED)
                .header("Location", "/users/" + userDto.getId())
                .build();
    }
}
