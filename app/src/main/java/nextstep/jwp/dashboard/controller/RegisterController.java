package nextstep.jwp.dashboard.controller;

import java.util.Map;

import nextstep.jwp.dashboard.service.UserService;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import nextstep.jwp.httpserver.exception.GlobalException;
import nextstep.jwp.httpserver.handler.controller.AbstractController;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController(UserService userService) {
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
            userService.join(parameters.get("account"), parameters.get("password"), parameters.get("email"));
            response.redirect("/index.html");
        } catch (GlobalException e) {
            response.redirect("/" + e.getStatusCode().getCode() + ".html");
        } catch (Exception e) {
            response.redirect("/500.html");
        }
    }
}
