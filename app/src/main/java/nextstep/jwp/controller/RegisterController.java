package nextstep.jwp.controller;


import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.RegisterService;


public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController() {
        this.registerService = new RegisterService();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.forward("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody body = request.getRequestBody();

        registerService.register(body);

        response.redirect("/index.html");
    }
}
