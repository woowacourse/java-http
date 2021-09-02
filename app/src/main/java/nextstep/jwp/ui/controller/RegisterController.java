package nextstep.jwp.ui.controller;

import nextstep.jwp.application.RegisterService;
import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected void doGet(HttpRequest request,HttpResponse response) {
        response.forward(request.getPath() + ".html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        registerService.register(account, password, email);
        response.sendRedirect("/index.html");
    }
}
