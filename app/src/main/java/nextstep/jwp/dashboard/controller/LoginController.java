package nextstep.jwp.dashboard.controller;

import java.util.Map;

import nextstep.jwp.dashboard.controller.dto.UserDto;
import nextstep.jwp.dashboard.service.UserService;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import nextstep.jwp.httpserver.exception.GlobalException;
import nextstep.jwp.httpserver.handler.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.ok();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            final Map<String, String> parameters = request.getBodyToMap();
            final UserDto userDto = userService.login(parameters.get("account"), parameters.get("password"));
            log.debug(userDto.toString());
            response.redirect("/index.html");
        } catch (GlobalException e) {
            response.redirect("/" + e.getStatusCode().getCode() + ".html");
        } catch (Exception e) {
            response.redirect("/500.html");
        }
    }
}
