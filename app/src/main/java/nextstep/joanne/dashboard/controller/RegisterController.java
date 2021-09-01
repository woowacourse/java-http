package nextstep.joanne.dashboard.controller;

import nextstep.joanne.dashboard.service.RegisterService;
import nextstep.joanne.server.handler.controller.AbstractController;
import nextstep.joanne.server.http.HttpStatus;
import nextstep.joanne.server.http.request.ContentType;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        registerService.join(request.bodyOf("account"), request.bodyOf("email"), request.bodyOf("password"));
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
