package nextstep.joanne.dashboard.controller;

import nextstep.joanne.dashboard.service.UserService;
import nextstep.joanne.server.handler.controller.AbstractController;
import nextstep.joanne.server.http.HttpStatus;
import nextstep.joanne.server.http.request.ContentType;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.response.HttpResponse;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        userService.login(request.bodyOf("account"), request.bodyOf("password"));
        response.addStatus(HttpStatus.FOUND);
        response.addHeaders("Location", "/index.html");
        response.addHeaders("Content-Type", ContentType.resolve(request.uri()));
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.addStatus(HttpStatus.OK);
        response.addHeaders("Content-Type", ContentType.resolve(request.uri()));
        response.addBody(request.uri());
    }
}
